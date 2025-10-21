package at.primetshofer.pekoNihongoBackend.service;

import at.primetshofer.pekoNihongoBackend.entity.*;
import at.primetshofer.pekoNihongoBackend.enums.MoneyReward;
import at.primetshofer.pekoNihongoBackend.enums.QuestCategory;
import at.primetshofer.pekoNihongoBackend.enums.QuestType;
import at.primetshofer.pekoNihongoBackend.enums.ShopItemType;
import at.primetshofer.pekoNihongoBackend.events.*;
import at.primetshofer.pekoNihongoBackend.repository.QuestRepository;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class QuestService {

    private final QuestRepository questRepository;
    private final StatsService statsService;
    private final MoneyService moneyService;
    private final UserService userService;

    public QuestService(QuestRepository questRepository, StatsService statsService, MoneyService moneyService, UserService userService) {
        this.questRepository = questRepository;
        this.statsService = statsService;
        this.moneyService = moneyService;
        this.userService = userService;
    }

    public List<Quest> getDailyQuests(Long userId){
        return questRepository.findByUserIdAndCategory(userId, QuestCategory.DAILY_QUEST);
    }

    public List<Quest> getAllQuests(Long userId){
        return questRepository.findByUserId((userId));
    }

    public Quest addQuest(Quest quest, User user){
        quest.setUser(user);
        quest.setProgress(0);

        if(quest.getType() == QuestType.CUSTOM){
            quest.setGoal(1);
        }
        if(quest.getType() == QuestType.DAILY_KANJI){
            quest.setGoal(user.getUserSettings().getMaxDailyKanji());
        }
        if(quest.getType() == QuestType.DAILY_WORDS){
            quest.setGoal(user.getUserSettings().getMaxDailyWords());
        }

        return questRepository.save(quest);
    }

    public Quest updateQuest(Quest quest, Long userId){
        Quest updatedQuest = questRepository.findByIdAndUserId(quest.getId(), userId);

        if(updatedQuest == null){
            return null;
        }

        updatedQuest.setGoal(quest.getGoal());
        updatedQuest.setProgress(quest.getProgress());
        updatedQuest.setText(quest.getText());

        Quest save = questRepository.save(updatedQuest);

        if(save.getProgress() >= save.getGoal()){
            checkAllDailyQuestsCompleted(userId);
        }

        return save;
    }

    public boolean deleteQuest(Long questId, Long userId){
        Quest oldQuest = questRepository.findByIdAndUserId(questId, userId);

        if(oldQuest == null){
            return false;
        }

        questRepository.deleteById(questId);

        checkAllDailyQuestsCompleted(userId);

        return true;
    }

    @Async
    @EventListener
    protected void handleWordDraftCreated(WordDraftCreatedEvent event){
        if(increaseQuestProgress(event.userId(), QuestType.NEW_DRAFTS, 1)){
            checkAllDailyQuestsCompleted(event.userId());
        }
    }

    @EventListener
    protected void handleDailyProgress(DailyProgressEvent event){
        QuestType type = null;

        if(event.type().equals(Kanji.class)){
            type = QuestType.DAILY_KANJI;
        } else if(event.type().equals(Word.class)){
            type = QuestType.DAILY_WORDS;
        }

        if(increaseQuestProgress(event.userId(), type, event.amount())){
            checkAllDailyQuestsCompleted(event.userId());
        }
    }

    @Async
    @EventListener
    protected void handleUpdateDailyGoal(UpdateDailyGoalEvent event){
        QuestType type = null;

        if(event.type().equals(Kanji.class)){
            type = QuestType.DAILY_KANJI;
        } else if(event.type().equals(Word.class)){
            type = QuestType.DAILY_WORDS;
        }

        List<Quest> quests = questRepository.findByUserIdAndType(event.userId(), type);

        if(quests.isEmpty()) return;

        for(Quest quest : quests){
            quest.setGoal(event.dailyGoal());
        }

        questRepository.saveAll(quests);

        checkAllDailyQuestsCompleted(event.userId());
    }

    @Async
    @Transactional
    @EventListener
    protected void handleFirstDailyLogin(FirstDailyLoginEvent event){
        resetAllDailyQuests(event.userId());
        removeExpiredQuests(event.userId());
        checkWeeklyQuests(event.userId());
    }

    @EventListener
    protected void handleExerciseFinished(ExerciseFinishedEvent event) {
        if(event.duration().toSeconds() > 30){
            moneyService.addMoneyReward(event.userId(), MoneyReward.EXERCISE_REWARD);
        }

        boolean success1 = increaseQuestProgress(event.userId(), QuestType.EXERCISE_COUNT, 1);
        boolean success2 = increaseQuestProgress(event.userId(), QuestType.EXERCISE_TIME, (int) event.duration().toSeconds());

        if(success1 || success2){
            checkAllDailyQuestsCompleted(event.userId());
        }
    }

    private boolean increaseQuestProgress(Long userId, QuestType type, int amount){
        List<Quest> quests = questRepository.findByUserIdAndType(userId, type);
        if(quests.isEmpty()) return false;

        boolean change = false;

        for(Quest quest : quests){
            int tmpAmount = amount;
            if(amount == -1 || amount > quest.getGoal() - quest.getProgress()){
                tmpAmount = quest.getGoal() - quest.getProgress();
            }
            change = change || tmpAmount > 0;
            quest.setProgress(quest.getProgress() + tmpAmount);

            if(tmpAmount > 0 && quest.getProgress() >= quest.getGoal()){
                questCompleted(quest, userId);
            }
        }

        if(change){
            questRepository.saveAll(quests);

            return true;
        }

        return false;
    }

    private void questCompleted(Quest quest, Long userId){
        if(quest.getCategory() == QuestCategory.CHALLENGE_QUEST){
            quest.setExpirationDate(LocalDate.now());
            moneyService.addMoneyReward(userId, ShopItemType.CHALLENGE_QUEST.price * 2);
        }
        if(quest.getCategory() == QuestCategory.WEEKLY_QUEST){
            moneyService.addMoneyReward(userId, MoneyReward.WEEKLY_QUEST_COMPLETE);
        }
    }

    private void resetAllDailyQuests(Long userId){
        List<Quest> dailyQuests = questRepository.findByUserIdAndCategory(userId, QuestCategory.DAILY_QUEST);

        for(Quest quest : dailyQuests){
            quest.setProgress(0);
        }

        questRepository.saveAll(dailyQuests);
    }

    private void checkWeeklyQuests(Long userId){
        List<Quest> weeklyQuests = questRepository.findByUserIdAndCategory(userId, QuestCategory.WEEKLY_QUEST);

        if(weeklyQuests.isEmpty()){
            createNewWeeklyQuests(userId);
        }
    }

    private void removeExpiredQuests(Long userId) {
        questRepository.findByUserIdAndExpirationDateBefore(userId, LocalDate.now()).forEach(quest ->
                questRepository.deleteById(quest.getId())
        );
    }

    private void checkAllDailyQuestsCompleted(Long userId){
        List<Quest> dailyQuests = questRepository.findByUserIdAndCategory(userId, QuestCategory.DAILY_QUEST);

        for(Quest quest : dailyQuests){
            if(quest.getProgress() < quest.getGoal()){
                return;
            }
        }

        if(statsService.increaseDailyQuestStreak(userId)){
            moneyService.addMoneyReward(userId, MoneyReward.DAILY_QUESTS_COMPLETE);
        }
    }

    public boolean hasActiveChallengeQuest(Long userId){
        List<Quest> dailyQuests = questRepository.findByUserIdAndCategory(userId, QuestCategory.CHALLENGE_QUEST);

        return !dailyQuests.isEmpty();
    }

    public boolean createRandomChallengeQuest(User user){
        Random random = new Random();
        Quest quest = new Quest();
        quest.setCategory(QuestCategory.CHALLENGE_QUEST);

        switch (random.nextInt(3)) {
            case 0 -> quest.setType(QuestType.EXERCISE_TIME);
            case 1 -> quest.setType(QuestType.EXERCISE_COUNT);
            case 2 -> quest.setType(QuestType.NEW_DRAFTS);
        }

        if(quest.getType() == QuestType.EXERCISE_TIME){
            quest.setGoal(random.nextInt(90, 180) * 60);
        }else if(quest.getType() == QuestType.EXERCISE_COUNT){
            quest.setGoal(random.nextInt(100, 300));
        }else if(quest.getType() == QuestType.NEW_DRAFTS){
            quest.setGoal(random.nextInt(50, 120));
        }
        quest.setExpirationDate(LocalDate.now().plusDays(random.nextInt(2, 7)));

        return addQuest(quest, user) != null;
    }

    protected void createNewWeeklyQuests(Long userId){
        User user = userService.getUserById(userId);
        LocalDate today = LocalDate.now();
        LocalDate nextSunday = today.with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
        Random random = new Random();
        Quest learnTime = new Quest();
        Quest learnCount = new Quest();
        Quest draftCount = new Quest();

        learnTime.setCategory(QuestCategory.WEEKLY_QUEST);
        learnCount.setCategory(QuestCategory.WEEKLY_QUEST);
        draftCount.setCategory(QuestCategory.WEEKLY_QUEST);

        learnTime.setType(QuestType.EXERCISE_TIME);
        learnCount.setType(QuestType.EXERCISE_COUNT);
        draftCount.setType(QuestType.NEW_DRAFTS);

        learnTime.setExpirationDate(nextSunday);
        learnCount.setExpirationDate(nextSunday);
        draftCount.setExpirationDate(nextSunday);

        learnTime.setGoal(random.nextInt(60, 240) * 60);
        learnCount.setGoal(random.nextInt(50, 300));
        draftCount.setGoal(random.nextInt(20, 60));

        addQuest(learnTime, user);
        addQuest(learnCount, user);
        addQuest(draftCount, user);
    }

}

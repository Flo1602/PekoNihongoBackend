package at.primetshofer.pekoNihongoBackend.service;

import at.primetshofer.pekoNihongoBackend.entity.*;
import at.primetshofer.pekoNihongoBackend.events.DailyProgressEvent;
import at.primetshofer.pekoNihongoBackend.events.FirstDailyLoginEvent;
import at.primetshofer.pekoNihongoBackend.events.UpdateDailyGoalEvent;
import at.primetshofer.pekoNihongoBackend.events.WordDraftCreatedEvent;
import at.primetshofer.pekoNihongoBackend.repository.QuestRepository;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestService {

    private final QuestRepository questRepository;
    private final StatsService statsService;

    public QuestService(QuestRepository questRepository, StatsService statsService) {
        this.questRepository = questRepository;
        this.statsService = statsService;
    }

    public List<Quest> getDailyQuests(Long userId){
        return questRepository.findByUserIdAndCategory(userId, QuestCategory.DAILY_QUEST);
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
        if(increaseQuestProgress(event.getUserId(), QuestType.NEW_DRAFTS, 1)){
            checkAllDailyQuestsCompleted(event.getUserId());
        }
    }

    @Async
    @EventListener
    protected void handleDailyProgress(DailyProgressEvent event){
        QuestType type = null;

        if(event.getType().equals(Kanji.class)){
            type = QuestType.DAILY_KANJI;
        } else if(event.getType().equals(Word.class)){
            type = QuestType.DAILY_WORDS;
        }

        if(increaseQuestProgress(event.getUserId(), type, event.getAmount())){
            checkAllDailyQuestsCompleted(event.getUserId());
        }
    }

    @Async
    @EventListener
    protected void handleUpdateDailyGoal(UpdateDailyGoalEvent event){
        QuestType type = null;

        if(event.getType().equals(Kanji.class)){
            type = QuestType.DAILY_KANJI;
        } else if(event.getType().equals(Word.class)){
            type = QuestType.DAILY_WORDS;
        }

        List<Quest> quests = questRepository.findByUserIdAndType(event.getUserId(), type);

        if(quests.isEmpty()) return;

        for(Quest quest : quests){
            quest.setGoal(event.getDailyGoal());
        }

        questRepository.saveAll(quests);

        checkAllDailyQuestsCompleted(event.getUserId());
    }

    @Async
    @EventListener
    protected void handleFirstDailyLogin(FirstDailyLoginEvent event){
        resetAllDailyQuests(event.getUserId());
    }

    private boolean increaseQuestProgress(Long userId, QuestType type, int amount){
        List<Quest> quests = questRepository.findByUserIdAndType(userId, type);
        if(quests.isEmpty()) return false;

        int tmpAmount = amount;
        boolean change = false;

        for(Quest quest : quests){
            if(amount == -1 || amount > quest.getGoal() - quest.getProgress()){
                tmpAmount = quest.getGoal() - quest.getProgress();
            }
            change = change || tmpAmount > 0;
            quest.setProgress(quest.getProgress() + tmpAmount);
        }

        if(change){
            questRepository.saveAll(quests);
            return true;
        }

        return false;
    }

    private void resetAllDailyQuests(Long userId){
        List<Quest> dailyQuests = questRepository.findByUserIdAndCategory(userId, QuestCategory.DAILY_QUEST);

        for(Quest quest : dailyQuests){
            quest.setProgress(0);
        }

        questRepository.saveAll(dailyQuests);
    }

    private void checkAllDailyQuestsCompleted(Long userId){
        List<Quest> dailyQuests = questRepository.findByUserIdAndCategory(userId, QuestCategory.DAILY_QUEST);

        for(Quest quest : dailyQuests){
            if(quest.getProgress() < quest.getGoal()){
                return;
            }
        }

        statsService.increaseDailyQuestStreak(userId);
    }

}

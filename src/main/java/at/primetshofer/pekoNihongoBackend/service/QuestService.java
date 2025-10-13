package at.primetshofer.pekoNihongoBackend.service;

import at.primetshofer.pekoNihongoBackend.entity.Quest;
import at.primetshofer.pekoNihongoBackend.entity.QuestCategory;
import at.primetshofer.pekoNihongoBackend.entity.QuestType;
import at.primetshofer.pekoNihongoBackend.entity.User;
import at.primetshofer.pekoNihongoBackend.repository.QuestRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestService {

    private final QuestRepository questRepository;

    public QuestService(QuestRepository questRepository) {
        this.questRepository = questRepository;
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

        return questRepository.save(updatedQuest);
    }

    public boolean deleteQuest(Long questId, Long userId){
        Quest oldQuest = questRepository.findByIdAndUserId(questId, userId);

        if(oldQuest == null){
            return false;
        }

        questRepository.deleteById(questId);

        return true;
    }

    public void increaseQuestProgress(Long userId, QuestType type, int amount){
        List<Quest> quests = questRepository.findByUserIdAndType(userId, type);

        int tmpAmount = amount;

        for(Quest quest : quests){
            if(amount == -1){
                tmpAmount = quest.getGoal() - quest.getProgress();
            }
            quest.setProgress(quest.getProgress() + tmpAmount);
        }

        questRepository.saveAll(quests);
    }

    public void increaseAndUpdateQuestProgress(Long userId, QuestType type, int progress, int goal){
        List<Quest> quests = questRepository.findByUserIdAndType(userId, type);

        for(Quest quest : quests){
            quest.setProgress(progress);
            quest.setGoal(goal);
        }

        questRepository.saveAll(quests);
    }

    public void resetAllDailyQuests(Long userId){
        List<Quest> dailyQuests = questRepository.findByUserIdAndCategory(userId, QuestCategory.DAILY_QUEST);

        for(Quest quest : dailyQuests){
            quest.setProgress(0);
        }

        questRepository.saveAll(dailyQuests);
    }

}

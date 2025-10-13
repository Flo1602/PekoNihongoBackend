package at.primetshofer.pekoNihongoBackend.dto;

import at.primetshofer.pekoNihongoBackend.entity.Quest;
import at.primetshofer.pekoNihongoBackend.entity.QuestCategory;
import at.primetshofer.pekoNihongoBackend.entity.QuestType;

public record QuestDto(Long id, QuestType type, QuestCategory category, String text, Integer goal, Integer progress) {
    public QuestDto(Quest quest){
        this(quest.getId(), quest.getType(), quest.getCategory(), quest.getText(), quest.getGoal(), quest.getProgress());
    }
}

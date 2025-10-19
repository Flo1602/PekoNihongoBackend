package at.primetshofer.pekoNihongoBackend.dto;

import at.primetshofer.pekoNihongoBackend.entity.Quest;
import at.primetshofer.pekoNihongoBackend.enums.QuestCategory;
import at.primetshofer.pekoNihongoBackend.enums.QuestType;

import java.time.LocalDate;

public record QuestDto(Long id, QuestType type, QuestCategory category, String text, Integer goal, Integer progress, LocalDate expirationDate) {
    public QuestDto(Quest quest){
        this(quest.getId(), quest.getType(), quest.getCategory(), quest.getText(), quest.getGoal(), quest.getProgress(), quest.getExpirationDate());
    }
}

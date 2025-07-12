package at.primetshofer.pekoNihongoBackend.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Embeddable
public class Progress {

    private Integer points;

    private LocalDate lastLearned;

    private LocalDate nextDueDate;

    private Integer learnedDays;

    private Integer negativeDays;

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public LocalDate getLastLearned() {
        return lastLearned;
    }

    public void setLastLearned(LocalDate lastLearned) {
        this.lastLearned = lastLearned;
    }

    public LocalDate getNextDueDate() {
        return nextDueDate;
    }

    public void setNextDueDate(LocalDate nextDueDate) {
        this.nextDueDate = nextDueDate;
    }

    public Integer getLearnedDays() {
        return learnedDays;
    }

    public void setLearnedDays(Integer learnedDays) {
        this.learnedDays = learnedDays;
    }

    public Integer getNegativeDays() {
        return negativeDays;
    }

    public void setNegativeDays(Integer negativeDays) {
        this.negativeDays = negativeDays;
    }
}

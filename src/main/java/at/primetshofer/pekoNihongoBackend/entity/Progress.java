package at.primetshofer.pekoNihongoBackend.entity;

import jakarta.persistence.Embeddable;

import java.time.LocalDate;

@Embeddable
public class Progress {

    private Integer points;

    private LocalDate lastLearned;

    private LocalDate nextDueDate;

    private LocalDate firstLearned;

    private Boolean isDueToday;

    private Integer learnedDays;

    private Integer penalty;

    public Boolean getDueToday() {
        return isDueToday;
    }

    public void setDueToday(Boolean dueToday) {
        isDueToday = dueToday;
    }

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

    public Integer getPenalty() {
        if(penalty == null) penalty = 0;
        return penalty;
    }

    public void setPenalty(Integer penalty) {
        if(penalty < 0) penalty = 0;
        if(penalty > 90) penalty = 90;
        this.penalty = penalty;
    }

    public LocalDate getFirstLearned() {
        return firstLearned;
    }

    public void setFirstLearned(LocalDate firstLearned) {
        this.firstLearned = firstLearned;
    }
}

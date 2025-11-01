package at.primetshofer.pekoNihongoBackend.entity;

import jakarta.persistence.*;

import java.time.Duration;
import java.time.LocalDate;

@Entity
public class LearnTimeStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
            fetch    = FetchType.LAZY,
            optional = false,
            cascade  = CascadeType.PERSIST
    )
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Duration duration;

    private int exercises;

    private Integer streak;

    public LearnTimeStats() {
    }

    public LearnTimeStats(User user, LocalDate date, Duration duration, int exercises) {
        this.user = user;
        this.date = date;
        this.duration = duration;
        this.exercises = exercises;
    }

    public LearnTimeStats(LocalDate date, Duration duration, int exercises) {
        this.date = date;
        this.duration = duration;
        this.exercises = exercises;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public int getExercises() {
        return exercises;
    }

    public void setExercises(int exercises) {
        this.exercises = exercises;
    }

    public Integer getStreak() {
        return streak;
    }

    public void setStreak(Integer streak) {
        this.streak = streak;
    }
}

package at.primetshofer.pekoNihongoBackend.entity;

import at.primetshofer.pekoNihongoBackend.enums.QuestCategory;
import at.primetshofer.pekoNihongoBackend.enums.QuestType;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Quest {

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

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "TEXT")
    private QuestType type;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "TEXT")
    private QuestCategory category;

    private String text;

    private Integer goal;

    private Integer progress;

    private LocalDate expirationDate;

    public Quest() {
    }

    public Quest(QuestType type, QuestCategory category, String text, Integer goal) {
        this.type = type;
        this.category = category;
        this.text = text;
        this.goal = goal;
    }

    public Quest(Long id,QuestType type, QuestCategory category, String text, Integer goal, Integer progress, LocalDate expirationDate) {
        this.id = id;
        this.type = type;
        this.category = category;
        this.text = text;
        this.goal = goal;
        this.progress = progress;
        this.expirationDate = expirationDate;
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

    public QuestType getType() {
        return type;
    }

    public void setType(QuestType type) {
        this.type = type;
    }

    public QuestCategory getCategory() {
        return category;
    }

    public void setCategory(QuestCategory category) {
        this.category = category;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getGoal() {
        return goal;
    }

    public void setGoal(Integer goal) {
        this.goal = goal;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }
}

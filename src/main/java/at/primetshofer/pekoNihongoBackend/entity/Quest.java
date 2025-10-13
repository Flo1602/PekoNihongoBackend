package at.primetshofer.pekoNihongoBackend.entity;

import jakarta.persistence.*;

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

    private QuestType type;

    private QuestCategory category;

    private String text;

    private Integer goal;

    private Integer progress;

    public Quest() {
    }

    public Quest(QuestType type, QuestCategory category, String text, Integer goal) {
        this.type = type;
        this.category = category;
        this.text = text;
        this.goal = goal;
    }

    public Quest(Long id,QuestType type, QuestCategory category, String text, Integer goal, Integer progress) {
        this.id = id;
        this.type = type;
        this.category = category;
        this.text = text;
        this.goal = goal;
        this.progress = progress;
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
}

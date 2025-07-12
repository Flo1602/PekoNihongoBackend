package at.primetshofer.pekoNihongoBackend.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(indexes = @Index(columnList = "nextDueDate"))
public class Kanji {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Progress progress;

    @Column(nullable = false)
    private char symbol;

    private boolean isLearned;

    @ManyToMany
    @JoinTable(
            name = "kanji_words",
            joinColumns = @JoinColumn(name = "kanji_id"),
            inverseJoinColumns = @JoinColumn(name = "word_id")
    )
    private List<Word> words = new ArrayList<>();

    @ManyToOne(
            fetch    = FetchType.LAZY,
            optional = false,
            cascade  = CascadeType.PERSIST
    )
    @JoinColumn(name = "user_id")
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Progress getProgress() {
        return progress;
    }

    public void setProgress(Progress progress) {
        this.progress = progress;
    }

    public char getSymbol() {
        return symbol;
    }

    public void setSymbol(char symbol) {
        this.symbol = symbol;
    }

    public List<Word> getWords() {
        return words;
    }

    public void setWords(List<Word> words) {
        this.words = words;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

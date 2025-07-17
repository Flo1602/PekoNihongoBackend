package at.primetshofer.pekoNihongoBackend.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(indexes = @Index(columnList = "nextDueDate"))
public class Word implements Learnable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Progress progress;

    @Column(nullable = false)
    private String japanese;

    @Column(nullable = false)
    private String english;

    private String kana;

    private String ttsPath;

    @ManyToMany(mappedBy = "words", cascade = CascadeType.PERSIST)
    private List<Kanji> kanjis = new ArrayList<>();

    @ManyToOne(
            fetch    = FetchType.LAZY,
            optional = false,
            cascade  = CascadeType.PERSIST
    )
    @JoinColumn(name = "user_id")
    private User user;

    public Word() {
    }

    public Word(Long id, String japanese, String english, String kana, User user) {
        this.id = id;
        this.japanese = japanese;
        this.english = english;
        this.kana = kana;
        this.user = user;
    }

    public Word(String japanese, String english, String kana, User user) {
        this.japanese = japanese;
        this.english = english;
        this.kana = kana;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Progress getProgress() {
        return progress;
    }

    @Override
    public void setProgress(Progress progress) {
        this.progress = progress;
    }

    public String getJapanese() {
        return japanese;
    }

    public void setJapanese(String japanese) {
        this.japanese = japanese;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public String getKana() {
        return kana;
    }

    public void setKana(String kana) {
        this.kana = kana;
    }

    public String getTtsPath() {
        return ttsPath;
    }

    public void setTtsPath(String ttsPath) {
        this.ttsPath = ttsPath;
    }

    public List<Kanji> getKanjis() {
        return kanjis;
    }

    public void setKanjis(List<Kanji> kanjis) {
        this.kanjis = kanjis;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

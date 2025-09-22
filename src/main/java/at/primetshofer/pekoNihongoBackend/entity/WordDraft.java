package at.primetshofer.pekoNihongoBackend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "word_drafts")
public class WordDraft {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String japanese;

    private String english;

    private String kana;

    @ManyToOne(
            fetch    = FetchType.LAZY,
            optional = false,
            cascade  = CascadeType.PERSIST
    )
    @JoinColumn(name = "user_id")
    private User user;

    public WordDraft() {
    }

    public WordDraft(Long id, String japanese, String english, String kana, User user) {
        this.id = id;
        this.japanese = japanese;
        this.english = english;
        this.kana = kana;
        this.user = user;
    }

    public WordDraft(String japanese, String english, String kana, User user) {
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

package at.primetshofer.pekoNihongoBackend.entity;

import at.primetshofer.pekoNihongoBackend.security.user.Role;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role userRole;

    @OneToMany(
            mappedBy      = "user",
            fetch         = FetchType.LAZY,
            cascade       = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<Kanji> kanjis = new HashSet<>();

    @OneToMany(
            mappedBy      = "user",
            fetch         = FetchType.LAZY,
            cascade       = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<Word> words = new HashSet<>();

    @OneToMany(
            mappedBy      = "user",
            fetch         = FetchType.LAZY,
            cascade       = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<LearnTimeStats> learnTimeStats = new HashSet<>();

    private Integer money;

    @Embedded
    private UserSettings userSettings;

    public User() {
    }

    public User(String username, String password, Role userRole) {
        this.username = username;
        this.password = password;
        this.userRole = userRole;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Kanji> getKanjis() {
        return kanjis;
    }

    public void setKanjis(Set<Kanji> kanjis) {
        this.kanjis = kanjis;
    }

    public Set<Word> getWords() {
        return words;
    }

    public void setWords(Set<Word> words) {
        this.words = words;
    }

    public Set<LearnTimeStats> getLearnTimeStats() {
        return learnTimeStats;
    }

    public void setLearnTimeStats(Set<LearnTimeStats> learnTimeStats) {
        this.learnTimeStats = learnTimeStats;
    }

    public UserSettings getUserSettings() {
        return userSettings;
    }

    public void setUserSettings(UserSettings userSettings) {
        this.userSettings = userSettings;
    }

    public Role getUserRole() {
        return userRole;
    }

    public void setUserRole(Role userRole) {
        this.userRole = userRole;
    }

    public Integer getMoney() {
        if(money == null) {
            money = 0;
        }
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }
}

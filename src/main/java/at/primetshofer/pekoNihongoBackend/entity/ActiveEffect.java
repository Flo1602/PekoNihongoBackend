package at.primetshofer.pekoNihongoBackend.entity;

import at.primetshofer.pekoNihongoBackend.enums.EffectType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class ActiveEffect {

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
    private EffectType type;

    private LocalDateTime expirationDateTime;

    public ActiveEffect() {
    }

    public ActiveEffect(Long id, User user, EffectType type, LocalDateTime expirationDateTime) {
        this.id = id;
        this.user = user;
        this.type = type;
        this.expirationDateTime = expirationDateTime;
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

    public EffectType getType() {
        return type;
    }

    public void setType(EffectType type) {
        this.type = type;
    }

    public LocalDateTime getExpirationDateTime() {
        return expirationDateTime;
    }

    public void setExpirationDateTime(LocalDateTime expirationDateTime) {
        this.expirationDateTime = expirationDateTime;
    }
}

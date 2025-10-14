package at.primetshofer.pekoNihongoBackend.events;

public class FirstDailyLoginEvent {
    private final Long userId;

    public FirstDailyLoginEvent(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
}

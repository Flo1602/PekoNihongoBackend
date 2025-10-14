package at.primetshofer.pekoNihongoBackend.events;

import at.primetshofer.pekoNihongoBackend.entity.Learnable;

public class DailyProgressEvent {
    private final Long userId;
    private final int amount;
    private final Class< ? extends Learnable> type;

    public DailyProgressEvent(Long userId, int amount, Class<? extends Learnable> type) {
        this.userId = userId;
        this.amount = amount;
        this.type = type;
    }

    public Long getUserId() {
        return userId;
    }

    public int getAmount() {
        return amount;
    }

    public Class<? extends Learnable> getType() {
        return type;
    }
}

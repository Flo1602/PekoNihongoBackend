package at.primetshofer.pekoNihongoBackend.events;

import at.primetshofer.pekoNihongoBackend.entity.Learnable;

public class UpdateDailyGoalEvent {
    private final Long userId;
    private final int dailyGoal;
    private final Class< ? extends Learnable> type;

    public UpdateDailyGoalEvent(Long userId, int dailyGoal, Class<? extends Learnable> type) {
        this.userId = userId;
        this.dailyGoal = dailyGoal;
        this.type = type;
    }

    public Long getUserId() {
        return userId;
    }

    public int getDailyGoal() {
        return dailyGoal;
    }

    public Class<? extends Learnable> getType() {
        return type;
    }
}

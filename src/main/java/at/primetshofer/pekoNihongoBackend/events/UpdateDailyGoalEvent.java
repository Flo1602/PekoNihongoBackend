package at.primetshofer.pekoNihongoBackend.events;

import at.primetshofer.pekoNihongoBackend.entity.Learnable;

public record UpdateDailyGoalEvent(Long userId, int dailyGoal, Class<? extends Learnable> type) {
}

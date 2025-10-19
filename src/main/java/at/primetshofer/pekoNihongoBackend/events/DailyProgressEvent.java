package at.primetshofer.pekoNihongoBackend.events;

import at.primetshofer.pekoNihongoBackend.entity.Learnable;

public record DailyProgressEvent(Long userId, int amount, Class<? extends Learnable> type) {
}

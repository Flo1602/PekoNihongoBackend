package at.primetshofer.pekoNihongoBackend.events;

import java.time.Duration;

public record ExerciseFinishedEvent(Long userId, Duration duration) {
}

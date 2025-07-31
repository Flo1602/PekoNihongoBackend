package at.primetshofer.pekoNihongoBackend.dto;

import java.time.Duration;
import java.util.List;

public record AllStatsDto(int kanjiCount, int wordsCount, Duration totalDuration, int totalExercises, List<StatsDto> stats) {
}

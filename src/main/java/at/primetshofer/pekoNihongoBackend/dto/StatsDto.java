package at.primetshofer.pekoNihongoBackend.dto;

import java.time.Duration;
import java.time.LocalDate;

public record StatsDto(LocalDate date, Duration duration, int exercises) {
}

package at.primetshofer.pekoNihongoBackend.dto.japneseLearningApp;

import java.time.LocalDateTime;

public record OldProgressDto(LocalDateTime learned, int points, int compressedEntries) {
}

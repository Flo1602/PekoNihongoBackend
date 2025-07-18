package at.primetshofer.pekoNihongoBackend.dto;

import java.util.List;

public record KanjiLearningDto(Long id, String symbol, List<WordDto> kanjiWords, List<WordDto> randomWords) {
}

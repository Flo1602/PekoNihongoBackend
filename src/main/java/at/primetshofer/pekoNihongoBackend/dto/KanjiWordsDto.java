package at.primetshofer.pekoNihongoBackend.dto;

import at.primetshofer.pekoNihongoBackend.entity.Kanji;
import at.primetshofer.pekoNihongoBackend.entity.Word;

import java.util.List;

public record KanjiWordsDto(Long id, char symbol, List<String> words) {
    public KanjiWordsDto(Kanji kanji){
        this(kanji.getId(), kanji.getSymbol(), kanji.getWords().stream().map(Word::getJapanese).toList());
    }
}

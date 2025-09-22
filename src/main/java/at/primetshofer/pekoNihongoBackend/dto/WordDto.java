package at.primetshofer.pekoNihongoBackend.dto;

import at.primetshofer.pekoNihongoBackend.entity.Word;
import at.primetshofer.pekoNihongoBackend.entity.WordDraft;

public record WordDto(Long id, String japanese, String english, String kana, String ttsPath) {
    public WordDto(Word word){
        this(word.getId(), word.getJapanese(), word.getEnglish(), word.getKana(), word.getTtsPath());
    }
    public WordDto(WordDraft wordDraft){
        this(wordDraft.getId(), wordDraft.getJapanese(), wordDraft.getEnglish(), wordDraft.getKana(), null);
    }
}

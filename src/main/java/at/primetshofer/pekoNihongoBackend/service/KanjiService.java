package at.primetshofer.pekoNihongoBackend.service;

import at.primetshofer.pekoNihongoBackend.entity.Kanji;
import at.primetshofer.pekoNihongoBackend.entity.Word;
import at.primetshofer.pekoNihongoBackend.repository.KanjiRepository;
import at.primetshofer.pekoNihongoBackend.utils.JapaneseUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class KanjiService {

    private final KanjiRepository kanjiRepository;

    public KanjiService(KanjiRepository kanjiRepository) {
        this.kanjiRepository = kanjiRepository;
    }

    public void unlinkKanji(Word word) {
        for (Kanji kanji : word.getKanjis()) {
            kanji.getWords().remove(word);
            kanjiRepository.save(kanji);
        }
        word.getKanjis().clear();
    }

    public void connectKanji(Word word) {
        List<Character> kanjis = JapaneseUtils.extractKanji(word.getJapanese());

        for (char kanji : kanjis) {
            List<Kanji> foundKanji = kanjiRepository.getAllBySymbolIsAndUserId(kanji, word.getUser().getId());

            Kanji k;
            if (!foundKanji.isEmpty()) {
                k = foundKanji.getFirst();
            } else {
                k = new Kanji();
                k.setSymbol(kanji);
                k.setUser(word.getUser());
            }

            if (!k.getWords().contains(word)) {
                k.getWords().add(word);
                k = kanjiRepository.save(k);
            }
            if (!word.getKanjis().contains(k)) {
                word.getKanjis().add(k);
            }
        }

        List<Kanji> removeKanjis = new ArrayList<>();
        for (Kanji kanji : word.getKanjis()) {
            if (!kanjis.contains(kanji.getSymbol())) {
                removeKanjis.add(kanji);
                kanji.getWords().remove(word);
                kanjiRepository.save(kanji);
            }
        }

        word.getKanjis().removeAll(removeKanjis);
    }
}

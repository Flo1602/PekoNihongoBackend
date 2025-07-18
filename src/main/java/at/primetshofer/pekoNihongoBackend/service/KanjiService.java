package at.primetshofer.pekoNihongoBackend.service;

import at.primetshofer.pekoNihongoBackend.entity.Kanji;
import at.primetshofer.pekoNihongoBackend.entity.Word;
import at.primetshofer.pekoNihongoBackend.repository.KanjiRepository;
import at.primetshofer.pekoNihongoBackend.utils.JapaneseUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class KanjiService {

    private final KanjiRepository kanjiRepository;

    public KanjiService(KanjiRepository kanjiRepository) {
        this.kanjiRepository = kanjiRepository;
    }

    public Kanji getBySymbol(Character symbol, Long userId) {
        return kanjiRepository.findByUserIdAndSymbol(userId, symbol);
    }

    public Kanji getById(Long kanjiId, Long userId) {
        return kanjiRepository.findByUserIdAndId(userId, kanjiId);
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

    public Page<Kanji> getKanji(int pageSize, int page, Long userId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        return kanjiRepository.findAllByUserId(userId, pageable);
    }

    public boolean deleteKanji(Long id, Long userId) {
        Optional<Kanji> kanji = kanjiRepository.findByIdAndUserId(id, userId);

        if (kanji.isEmpty()) {
            return false;
        }
        if (!kanji.get().getWords().isEmpty()) {
            return false;
        }

        kanjiRepository.deleteById(id);
        return true;
    }
}

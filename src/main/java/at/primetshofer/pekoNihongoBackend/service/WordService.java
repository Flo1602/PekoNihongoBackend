package at.primetshofer.pekoNihongoBackend.service;

import at.primetshofer.pekoNihongoBackend.config.WebMvcConfig;
import at.primetshofer.pekoNihongoBackend.entity.Word;
import at.primetshofer.pekoNihongoBackend.repository.WordRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class WordService {

    private final WordRepository wordRepository;
    private final KanjiService kanjiService;
    private final AudioService audioService;

    public WordService(WordRepository wordRepository, KanjiService kanjiService, AudioService audioService) {
        this.wordRepository = wordRepository;
        this.kanjiService = kanjiService;
        this.audioService = audioService;
    }

    public Word addWord(Word word) {
        Word dbWord = wordRepository.save(word);
        kanjiService.connectKanji(word);

        audioService.addAudioToWord(word);

        return dbWord;
    }

    public Word updateWord(Word newWord){
        if (newWord == null) {
            throw new IllegalArgumentException("Word is null");
        }

        Word updatedWord = wordRepository.findById(newWord.getId()).orElse(null);

        if (updatedWord == null){
            throw new IllegalArgumentException("Word with id " + newWord.getId() + " does not exist");
        }

        deleteAudioFile(updatedWord);

        updatedWord.setJapanese(newWord.getJapanese());
        updatedWord.setKana(newWord.getKana());
        updatedWord.setEnglish(newWord.getEnglish());
        updatedWord.setTtsPath(null);

        Word dbWord = wordRepository.save(updatedWord);

        kanjiService.connectKanji(dbWord);
        audioService.addAudioToWord(dbWord);

        return dbWord;
    }

    public boolean deleteWord(Long wordId, Long userId) {
        Optional<Word> word = wordRepository.findByIdAndUserId(wordId, userId);
        if (word.isEmpty()) {
            return false;
        }

        kanjiService.unlinkKanji(word.get());

        deleteAudioFile(word.get());

        wordRepository.deleteById(wordId);
        return true;
    }

    private void deleteAudioFile(Word word){
        if (word.getTtsPath() != null) {
            try {
                File audioFile = new java.io.File((WebMvcConfig.AUDIO_PATH + "/" + word.getTtsPath()));
                audioFile.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public List<Word> getWords(Long userId, Collection<Long> wordIds) {
        return wordRepository.getWordsByUserIdAndIdIn(userId, wordIds);
    }

    public Page<Word> getWords(int pageSize, int page, Long userId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        return wordRepository.findAllByUserId(userId, pageable);
    }

    public List<Word> getWords(int count, Long currentUserId) {
        return wordRepository.getWordsByUserId(currentUserId, Limit.of(count));
    }
}

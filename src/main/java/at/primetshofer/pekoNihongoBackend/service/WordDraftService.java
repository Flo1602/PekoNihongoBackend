package at.primetshofer.pekoNihongoBackend.service;

import at.primetshofer.pekoNihongoBackend.dto.WordDto;
import at.primetshofer.pekoNihongoBackend.entity.User;
import at.primetshofer.pekoNihongoBackend.entity.Word;
import at.primetshofer.pekoNihongoBackend.entity.WordDraft;
import at.primetshofer.pekoNihongoBackend.repository.WordDraftRepository;
import at.primetshofer.pekoNihongoBackend.utils.JapaneseUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WordDraftService {

    private static final String JISHO_URL = "https://jisho.org/search/";

    private final WordDraftRepository wordDraftRepository;
    private final WordService wordService;

    public WordDraftService(WordDraftRepository wordDraftRepository, WordService wordService) {
        this.wordDraftRepository = wordDraftRepository;
        this.wordService = wordService;
    }

    public WordDraft addWord(WordDraft word) {
        return wordDraftRepository.save(word);
    }

    public WordDraft updateWord(WordDraft newWord){
        if (newWord == null) {
            throw new IllegalArgumentException("WordDraft is null");
        }

        WordDraft updatedWord = wordDraftRepository.findById(newWord.getId()).orElse(null);

        if (updatedWord == null){
            throw new IllegalArgumentException("WordDraft with id " + newWord.getId() + " does not exist");
        }

        updatedWord.setJapanese(newWord.getJapanese());
        updatedWord.setKana(newWord.getKana());
        updatedWord.setEnglish(newWord.getEnglish());

        return wordDraftRepository.save(updatedWord);
    }

    public boolean deleteWord(Long wordDraftId, Long userId) {
        Optional<WordDraft> word = wordDraftRepository.findByIdAndUserId(wordDraftId, userId);
        if (word.isEmpty()) {
            return false;
        }

        wordDraftRepository.deleteById(wordDraftId);
        return true;
    }

    public Page<WordDraft> getWords(int pageSize, int page, Long userId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, pageSize, sort);

        return wordDraftRepository.findAllByUserId(userId, pageable);
    }

    public boolean setDraftAsActiveVocab(Long wordDraftId, User user){
        WordDraft wordDraft = wordDraftRepository.findById(wordDraftId).orElse(null);

        if(wordDraft == null){
            return false;
        }
        if(wordDraft.getId() == null || wordDraft.getJapanese() == null || wordDraft.getEnglish() == null || wordDraft.getKana() == null){
            return false;
        }
        if(wordDraft.getJapanese().trim().isBlank() || wordDraft.getEnglish().trim().isBlank() || wordDraft.getKana().trim().isBlank()){
            return false;
        }

        Word word = wordService.addWord(new Word(wordDraft.getJapanese(), wordDraft.getEnglish(), wordDraft.getKana(), user), user.getUserSettings().getUseAlwaysVoiceVox());

        if(word != null && word.getId() != null){
            wordDraftRepository.deleteById(wordDraftId);
            return true;
        }

        return false;
    }

    public List<WordDto> searchWordOnJisho(String search, int resultCount){
        String searchUrl = JISHO_URL + search;
        List<WordDto> wordDtos = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(searchUrl).get();

            for (Element entry : doc.select(".concept_light")) {
                Element japaneseElement = entry.selectFirst(".text");
                Element furiganaElement = entry.selectFirst(".furigana");
                Element englishElement = entry.selectFirst(".meanings-wrapper");

                if(japaneseElement == null || furiganaElement == null || englishElement == null){
                    continue;
                }

                String japanese = japaneseElement.text();

                int cntr = 0;
                StringBuilder kana = new StringBuilder();
                for (Element furiganaChild : furiganaElement.children()) {
                    String furiganaChildText = furiganaChild.text();
                    if(!furiganaChildText.trim().isBlank()){
                        kana.append(furiganaChildText);
                    } else {
                        kana.append(japaneseElement.children().get(cntr).text());
                        cntr++;
                    }
                }

                StringBuilder english = new StringBuilder();
                cntr = 0;
                for(Element englishChild : englishElement.select(".meaning-definition > .meaning-meaning:not(:has(.break-unit))")){
                    if(!english.isEmpty()){
                        english.append(", ");
                    }
                    english.append(englishChild.text().split(";")[0]);
                    cntr++;
                    if(cntr >= 3){
                        break;
                    }
                }

                wordDtos.add(new WordDto(null, japanese, english.toString(), kana.toString(), null));

                if(wordDtos.size() >= resultCount){
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            //logger.error("Failed to fetch Jisho URL '" + searchUrl + "'", ex);
        }

        return wordDtos;
    }
}

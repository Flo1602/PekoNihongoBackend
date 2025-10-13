package at.primetshofer.pekoNihongoBackend.service;

import at.primetshofer.pekoNihongoBackend.dto.KanjiInfoDto;
import at.primetshofer.pekoNihongoBackend.dto.WordDto;
import at.primetshofer.pekoNihongoBackend.dto.WordInfoDto;
import at.primetshofer.pekoNihongoBackend.entity.QuestType;
import at.primetshofer.pekoNihongoBackend.entity.User;
import at.primetshofer.pekoNihongoBackend.entity.Word;
import at.primetshofer.pekoNihongoBackend.entity.WordDraft;
import at.primetshofer.pekoNihongoBackend.repository.WordDraftRepository;
import at.primetshofer.pekoNihongoBackend.utils.JapaneseUtils;
import at.primetshofer.pekoNihongoBackend.utils.KanaConverter;
import at.primetshofer.pekoNihongoBackend.utils.JlptInfoUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WordDraftService {

    private static final String JISHO_URL = "https://jisho.org/search/";

    private final WordDraftRepository wordDraftRepository;
    private final WordService wordService;
    private final KanjiService kanjiService;
    private final QuestService questService;

    public WordDraftService(WordDraftRepository wordDraftRepository, WordService wordService, KanjiService kanjiService, QuestService questService) {
        this.wordDraftRepository = wordDraftRepository;
        this.wordService = wordService;
        this.kanjiService = kanjiService;
        this.questService = questService;
    }

    public WordDraft addWord(WordDraft word) {
        WordDraft wordDraft = wordDraftRepository.save(word);

        if(wordDraft.getId() != null){
            questService.increaseQuestProgress(word.getUser().getId(), QuestType.NEW_DRAFTS, 1);
        }

        return wordDraft;
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
        if(wordService.isWordInVocabs(wordDraft.getJapanese(), user.getId())){
            return false;
        }

        Word word = wordService.addWord(new Word(wordDraft.getJapanese(), wordDraft.getEnglish(), wordDraft.getKana(), user), user.getUserSettings().getUseAlwaysVoiceVox());

        if(word != null && word.getId() != null){
            wordDraftRepository.deleteById(wordDraftId);
            return true;
        }

        return false;
    }

    public List<WordDto> searchWordOnJisho(String search, int resultCount, Boolean convertToKana){
        search = search.toLowerCase().trim();
        if(convertToKana != null && convertToKana){
            search = KanaConverter.katakanaToHiragana(JapaneseUtils.convertKanjiToKatakana(search));
        }

        if(search.length() > 20){
            search = search.substring(0, 20);
        }

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
                    } else if(japaneseElement.children().size() > cntr) {
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

    public WordInfoDto getWordInfo(Long id, Long userId) {
        WordDraft wordDraft = wordDraftRepository.findByIdAndUserId(id, userId).orElse(null);
        if (wordDraft == null) return null;

        String jp = Optional.ofNullable(wordDraft.getJapanese()).map(String::trim).orElse("");
        if (jp.isBlank()) return null;

        HttpClient http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        ObjectMapper mapper = new ObjectMapper();

        String wordJlptInfo = JlptInfoUtils.fetchWordJlpt(http, mapper, jp);

        List<Character> kanjiChars = JapaneseUtils.extractKanji(jp);
        List<KanjiInfoDto> kanjiInfoDtos = new ArrayList<>();
        for (Character k : kanjiChars) {
            String jlpt = JlptInfoUtils.fetchKanjiJlpt(http, mapper, k.toString());
            boolean learned = kanjiService.isKanjiInVocabs(k, userId);
            kanjiInfoDtos.add(new KanjiInfoDto(k.toString(), jlpt != null ? jlpt : "JLPT -", learned));
        }

        String link = "https://jisho.org/search/" + URLEncoder.encode(jp, StandardCharsets.UTF_8);

        return new WordInfoDto(
                jp,
                link,
                wordJlptInfo != null ? wordJlptInfo : "JLPT -",
                kanjiInfoDtos
        );
    }
}

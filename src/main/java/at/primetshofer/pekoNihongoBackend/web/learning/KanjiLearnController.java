package at.primetshofer.pekoNihongoBackend.web.learning;

import at.primetshofer.pekoNihongoBackend.dto.KanjiLearningDto;
import at.primetshofer.pekoNihongoBackend.dto.ProgressDataDto;
import at.primetshofer.pekoNihongoBackend.dto.ProgressDto;
import at.primetshofer.pekoNihongoBackend.dto.WordDto;
import at.primetshofer.pekoNihongoBackend.entity.*;
import at.primetshofer.pekoNihongoBackend.repository.KanjiProgressRepository;
import at.primetshofer.pekoNihongoBackend.security.authentication.AuthConstants;
import at.primetshofer.pekoNihongoBackend.service.KanjiService;
import at.primetshofer.pekoNihongoBackend.service.TrainerService;
import at.primetshofer.pekoNihongoBackend.service.WordService;
import at.primetshofer.pekoNihongoBackend.utils.WebUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/learning/kanji")
@SecurityRequirement(name = AuthConstants.SECURITY_SCHEME_NAME)
public class KanjiLearnController {

    private final TrainerService trainerService;
    private final WebUtils webUtils;
    private final KanjiProgressRepository kanjiProgressRepository;
    private final KanjiService kanjiService;
    private final WordService wordService;

    public KanjiLearnController(TrainerService trainerService,
                                WebUtils webUtils,
                                KanjiProgressRepository kanjiProgressRepository,
                                KanjiService kanjiService,
                                WordService wordService) {
        this.trainerService = trainerService;
        this.webUtils = webUtils;
        this.kanjiProgressRepository = kanjiProgressRepository;
        this.kanjiService = kanjiService;
        this.wordService = wordService;
    }

    @GetMapping
    public KanjiLearningDto getLearningKanji(@RequestParam int wordCount) {
        User user = webUtils.getCurrentUser();

        Kanji kanji = trainerService.getDueElements(kanjiProgressRepository, 1, user.getId(), user.getUserSettings().getMaxDailyKanji()).getFirst();
        List<Word> words = kanji.getWords();
        Collections.shuffle(words);

        Progress progress = kanji.getProgress();
        int learnedDays = 0;
        if(progress != null) {
            learnedDays = progress.getLearnedDays();
        }

        return new KanjiLearningDto(kanji.getId(),
                kanji.getSymbol() + "",
                words.stream().map(WordDto::new).toList(),
                wordService.getRandomWordsWithKanji(wordCount, user.getId()).stream().map(WordDto::new).toList(),
                learnedDays);
    }

    @PostMapping
    public void saveProgress(@RequestBody List<ProgressDto> progresses) {
        long id = progresses.getFirst().id();
        int correct = 0;

        for (ProgressDto progress : progresses) {
            correct += progress.correct() ? 1 : 0;
        }

        correct *= 100;
        correct /= progresses.size();

        if(correct == 100) correct = 200;

        User user = webUtils.getCurrentUser();

        Kanji kanji = kanjiService.getById(id, user.getId());

        trainerService.saveProgress(kanji, kanjiProgressRepository, correct, user.getId());
    }

    @GetMapping("/progress")
    public ProgressDataDto getDueCount() {
        User user = webUtils.getCurrentUser();

        return trainerService.progressDataDto(kanjiProgressRepository, user.getId(), user.getUserSettings().getMaxDailyKanji());
    }
}

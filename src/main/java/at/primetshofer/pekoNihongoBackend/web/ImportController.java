package at.primetshofer.pekoNihongoBackend.web;

import at.primetshofer.pekoNihongoBackend.dto.StatsDto;
import at.primetshofer.pekoNihongoBackend.dto.WordDto;
import at.primetshofer.pekoNihongoBackend.dto.japneseLearningApp.OldKanjiDto;
import at.primetshofer.pekoNihongoBackend.dto.japneseLearningApp.OldWordDto;
import at.primetshofer.pekoNihongoBackend.entity.Kanji;
import at.primetshofer.pekoNihongoBackend.entity.LearnTimeStats;
import at.primetshofer.pekoNihongoBackend.entity.User;
import at.primetshofer.pekoNihongoBackend.entity.Word;
import at.primetshofer.pekoNihongoBackend.repository.KanjiProgressRepository;
import at.primetshofer.pekoNihongoBackend.repository.WordProgressRepository;
import at.primetshofer.pekoNihongoBackend.security.authentication.AuthConstants;
import at.primetshofer.pekoNihongoBackend.service.KanjiService;
import at.primetshofer.pekoNihongoBackend.service.StatsService;
import at.primetshofer.pekoNihongoBackend.service.TrainerService;
import at.primetshofer.pekoNihongoBackend.service.WordService;
import at.primetshofer.pekoNihongoBackend.utils.WebUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/api/import")
@SecurityRequirement(name = AuthConstants.SECURITY_SCHEME_NAME)
public class ImportController {

    private final TrainerService trainerService;
    private final WordProgressRepository wordProgressRepository;
    private final KanjiProgressRepository kanjiProgressRepository;
    private final WordService wordService;
    private final KanjiService kanjiService;
    private final StatsService statsService;
    private final WebUtils webUtils;

    public ImportController(TrainerService trainerService,
                            WordProgressRepository wordProgressRepository,
                            WordService wordService,
                            WebUtils webUtils,
                            KanjiService kanjiService,
                            KanjiProgressRepository kanjiProgressRepository,
                            StatsService statsService) {
        this.trainerService = trainerService;
        this.wordProgressRepository = wordProgressRepository;
        this.wordService = wordService;
        this.webUtils = webUtils;
        this.kanjiService = kanjiService;
        this.kanjiProgressRepository = kanjiProgressRepository;
        this.statsService = statsService;
    }

    @PostMapping("/word")
    public void importWord(@RequestBody OldWordDto oldWordDto) {
        User currentUser = webUtils.getCurrentUser();
        Word word = wordService.addWord(new Word(oldWordDto.japanese(), oldWordDto.english(), oldWordDto.kana(), currentUser));
        trainerService.importOldData(word, Arrays.asList(oldWordDto.progress()), wordProgressRepository);
    }

    @PostMapping("/kanji")
    public boolean importKanji(@RequestBody OldKanjiDto oldKanjiDto) {
        User currentUser = webUtils.getCurrentUser();
        Kanji kanji = kanjiService.getBySymbol(oldKanjiDto.symbol().toCharArray()[0], currentUser.getId());

        if(kanji == null) {
            return false;
        }

        trainerService.importOldData(kanji, Arrays.asList(oldKanjiDto.progress()), kanjiProgressRepository);
        return true;
    }

    @PostMapping("/stats")
    public void importStats(@RequestBody StatsDto statsDto) {
        User currentUser = webUtils.getCurrentUser();
        statsService.addStat(new LearnTimeStats(statsDto.date(), statsDto.duration(), statsDto.exercises()), currentUser);
    }
}

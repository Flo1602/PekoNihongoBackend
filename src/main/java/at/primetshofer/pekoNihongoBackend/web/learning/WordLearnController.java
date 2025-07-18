package at.primetshofer.pekoNihongoBackend.web.learning;

import at.primetshofer.pekoNihongoBackend.dto.ProgressDataDto;
import at.primetshofer.pekoNihongoBackend.dto.ProgressDto;
import at.primetshofer.pekoNihongoBackend.dto.WordDto;
import at.primetshofer.pekoNihongoBackend.entity.User;
import at.primetshofer.pekoNihongoBackend.entity.Word;
import at.primetshofer.pekoNihongoBackend.repository.WordProgressRepository;
import at.primetshofer.pekoNihongoBackend.security.authentication.AuthConstants;
import at.primetshofer.pekoNihongoBackend.service.TrainerService;
import at.primetshofer.pekoNihongoBackend.service.WordService;
import at.primetshofer.pekoNihongoBackend.utils.WebUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/learning/words")
@SecurityRequirement(name = AuthConstants.SECURITY_SCHEME_NAME)
public class WordLearnController {

    private final TrainerService trainerService;
    private final WebUtils webUtils;
    private final WordProgressRepository wordProgressRepository;
    private final WordService wordService;

    public WordLearnController(TrainerService trainerService, WebUtils webUtils, WordProgressRepository wordProgressRepository, WordService wordService) {
        this.trainerService = trainerService;
        this.webUtils = webUtils;
        this.wordProgressRepository = wordProgressRepository;
        this.wordService = wordService;
    }

    @GetMapping
    public List<WordDto> getLearningWords(@RequestParam int count) {
        User user = webUtils.getCurrentUser();

        List<Word> words = trainerService.getDueElements(wordProgressRepository, count, user.getId(), user.getUserSettings().getMaxDailyWords());

        return words.stream().map(WordDto::new).toList();
    }

    @PostMapping
    public void saveProgress(@RequestBody List<ProgressDto> progresses) {
        User user = webUtils.getCurrentUser();
        Map<Long, List<ProgressDto>> byWordId = progresses.stream().collect(Collectors.groupingBy(ProgressDto::id));

        Set<Long> ids = byWordId.keySet();
        List<Word> words = wordService.getWords(user.getId(), ids);

        Map<Word, Integer> wordResult = words.stream().collect(Collectors.toMap(Function.identity(), w -> {
            List<ProgressDto> list = byWordId.getOrDefault(w.getId(), List.of());
            int total = list.size();
            if (total == 0) return 0;
            long correctCount = list.stream().filter(ProgressDto::correct).count();
            return (int) (correctCount * 100L / total);
        }));

        wordResult.forEach((word, percent) ->
                trainerService.saveProgress(word, wordProgressRepository, percent)
        );
    }

    @GetMapping("/progress")
    public ProgressDataDto getDueCount() {
        User user = webUtils.getCurrentUser();

        return trainerService.ProgressDataDto(wordProgressRepository, user);
    }

}

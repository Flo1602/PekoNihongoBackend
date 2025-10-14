package at.primetshofer.pekoNihongoBackend.web;

import at.primetshofer.pekoNihongoBackend.dto.AllStatsDto;
import at.primetshofer.pekoNihongoBackend.dto.StatsDto;
import at.primetshofer.pekoNihongoBackend.entity.LearnTimeStats;
import at.primetshofer.pekoNihongoBackend.security.authentication.AuthConstants;
import at.primetshofer.pekoNihongoBackend.service.StatsService;
import at.primetshofer.pekoNihongoBackend.utils.WebUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/stats")
@SecurityRequirement(name = AuthConstants.SECURITY_SCHEME_NAME)
public class StatsController {

    private final WebUtils webUtils;
    private final StatsService statsService;

    public StatsController(WebUtils webUtils, StatsService statsService) {
        this.webUtils = webUtils;
        this.statsService = statsService;
    }

    @GetMapping
    public AllStatsDto getAllStats(@RequestParam int count) {
        Long userId = webUtils.getCurrentUserId();

        int kanjiCount = statsService.getKanjiCount(userId);
        int wordCount = statsService.getWordCount(userId);
        Duration totalLearnTime = statsService.getTotalLearnTime(userId);
        int totalExercises = statsService.getTotalExercises(userId);
        List<LearnTimeStats> lastStats = statsService.getLastStats(count, userId);
        List<StatsDto> lastStatsDto = lastStats.stream().map(stat -> new StatsDto(
                stat.getDate(),
                stat.getDuration(),
                stat.getExercises(),
                stat.getStreak())
        ).toList();

        return new AllStatsDto(kanjiCount, wordCount, totalLearnTime, totalExercises, lastStatsDto);
    }

    @GetMapping("/between")
    public List<StatsDto> getBetweenStats(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        List<LearnTimeStats> stats = statsService.getStats(startDate, endDate, webUtils.getCurrentUserId());

        return stats.stream().map(stat -> new StatsDto(
                stat.getDate(),
                stat.getDuration(),
                stat.getExercises(),
                stat.getStreak())
        ).toList();
    }

    @PostMapping
    public void add(@RequestBody StatsDto statsDto) {
        statsService.addStat(statsDto.duration(), webUtils.getCurrentUser());
    }

}

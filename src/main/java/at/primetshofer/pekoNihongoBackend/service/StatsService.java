package at.primetshofer.pekoNihongoBackend.service;

import at.primetshofer.pekoNihongoBackend.entity.LearnTimeStats;
import at.primetshofer.pekoNihongoBackend.entity.User;
import at.primetshofer.pekoNihongoBackend.repository.KanjiRepository;
import at.primetshofer.pekoNihongoBackend.repository.LearnTimeStatsRepository;
import at.primetshofer.pekoNihongoBackend.repository.WordRepository;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Service
public class StatsService {

    private final LearnTimeStatsRepository statsRepository;
    private final KanjiRepository kanjiRepository;
    private final WordRepository wordRepository;

    public StatsService(LearnTimeStatsRepository statsRepository, KanjiRepository kanjiRepository, WordRepository wordRepository) {
        this.statsRepository = statsRepository;
        this.kanjiRepository = kanjiRepository;
        this.wordRepository = wordRepository;
    }

    public void addStat(Duration duration, User user) {
        LocalDate today = LocalDate.now();

        LearnTimeStats currStat = statsRepository.findByUserIdAndDate(user.getId(), today);

        if(currStat == null){
            currStat = new LearnTimeStats();
            currStat.setDuration(duration);
            currStat.setDate(today);
            currStat.setExercises(1);
            currStat.setUser(user);
        } else {
            currStat.setDuration(currStat.getDuration().plus(duration));
            currStat.setExercises(currStat.getExercises() + 1);
        }

        statsRepository.save(currStat);
    }

    public void addStat(LearnTimeStats stats, User user){
        stats.setUser(user);
        statsRepository.save(stats);
    }

    public int getKanjiCount(long userId){
        return kanjiRepository.countKanjiByUserId(userId);
    }

    public int getWordCount(long userId){
        return wordRepository.countWordsByUserId(userId);
    }

    public Duration getTotalLearnTime(long userId){
        return Duration.ofNanos(statsRepository.sumDurationNanos(userId));
    }

    public int getTotalExercises(long userId){
        return statsRepository.sumExercises(userId);
    }

    public List<LearnTimeStats> getLastStats(int count, long userId){
        return statsRepository.findByUserId(userId, Sort.by(Sort.Direction.DESC, "date"), Limit.of(count));
    }
}

package at.primetshofer.pekoNihongoBackend.service;

import at.primetshofer.pekoNihongoBackend.entity.LearnTimeStats;
import at.primetshofer.pekoNihongoBackend.entity.User;
import at.primetshofer.pekoNihongoBackend.repository.LearnTimeStatsRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;

@Service
public class StatsService {

    LearnTimeStatsRepository statsRepository;

    public StatsService(LearnTimeStatsRepository statsRepository) {
        this.statsRepository = statsRepository;
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
}

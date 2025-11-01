package at.primetshofer.pekoNihongoBackend.service;

import at.primetshofer.pekoNihongoBackend.entity.LearnTimeStats;
import at.primetshofer.pekoNihongoBackend.entity.User;
import at.primetshofer.pekoNihongoBackend.events.ExerciseFinishedEvent;
import at.primetshofer.pekoNihongoBackend.events.FirstDailyLoginEvent;
import at.primetshofer.pekoNihongoBackend.repository.KanjiRepository;
import at.primetshofer.pekoNihongoBackend.repository.LearnTimeStatsRepository;
import at.primetshofer.pekoNihongoBackend.repository.WordRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

@Service
public class StatsService {

    private final LearnTimeStatsRepository statsRepository;
    private final KanjiRepository kanjiRepository;
    private final WordRepository wordRepository;
    private final ApplicationEventPublisher eventPublisher;

    public StatsService(LearnTimeStatsRepository statsRepository, KanjiRepository kanjiRepository, WordRepository wordRepository, ApplicationEventPublisher eventPublisher) {
        this.statsRepository = statsRepository;
        this.kanjiRepository = kanjiRepository;
        this.wordRepository = wordRepository;
        this.eventPublisher = eventPublisher;
    }

    public void addStat(Duration duration, User user) {
        LocalDate today = LocalDate.now();

        LearnTimeStats currStat = statsRepository.findByUserIdAndDate(user.getId(), today);

        if (duration.isZero() && currStat != null) return;

        if (currStat == null) {
            currStat = new LearnTimeStats();
            currStat.setDuration(duration);
            currStat.setDate(today);
            currStat.setExercises((duration.isZero()) ? 0 : 1);
            currStat.setUser(user);

            if (duration.isZero()) {
                eventPublisher.publishEvent(new FirstDailyLoginEvent(user.getId()));
            }
        } else {
            currStat.setDuration(currStat.getDuration().plus(duration));
            currStat.setExercises(currStat.getExercises() + 1);
        }

        if (!duration.isZero()) {
            eventPublisher.publishEvent(new ExerciseFinishedEvent(user.getId(), duration));
        }

        statsRepository.save(currStat);
    }

    public void addStat(LearnTimeStats stats, User user) {
        stats.setUser(user);
        statsRepository.save(stats);
    }

    public int getKanjiCount(long userId) {
        return kanjiRepository.countKanjiByUserId(userId);
    }

    public int getWordCount(long userId) {
        return wordRepository.countWordsByUserId(userId);
    }

    public Duration getTotalLearnTime(long userId) {
        return Duration.ofNanos(statsRepository.sumDurationNanos(userId));
    }

    public int getTotalExercises(long userId) {
        return statsRepository.sumExercises(userId);
    }

    public List<LearnTimeStats> getLastStats(int count, Long userId) {
        return statsRepository.findByUserId(userId, Sort.by(Sort.Direction.DESC, "date"), Limit.of(count));
    }

    public List<LearnTimeStats> getStats(LocalDate from, LocalDate to, Long userId) {
        return statsRepository.findByUserIdAndDateBetween(userId, from, to);
    }

    public List<LearnTimeStats> getStats(LocalDate from, LocalDate to, Long userId, Sort sort) {
        return statsRepository.findByUserIdAndDateBetween(userId, from, to, sort);
    }

    private LearnTimeStats getStat(LocalDate date, Long userId) {
        return statsRepository.findByUserIdAndDate(userId, date);
    }

    public boolean increaseDailyQuestStreak(Long userId) {
        LearnTimeStats today = getStat(LocalDate.now(), userId);
        LearnTimeStats yesterday = getStat(LocalDate.now().minusDays(1), userId);

        if (today == null || (today.getStreak() != null && today.getStreak() > 0)) {
            return false;
        }

        if (yesterday == null || yesterday.getStreak() == null || yesterday.getStreak() < 1) {
            today.setStreak(1);
        } else {
            today.setStreak(yesterday.getStreak() + 1);
        }

        statsRepository.save(today);

        return true;
    }

    public boolean isStreakExtended(Long userId) {
        LearnTimeStats today = getStat(LocalDate.now(), userId);

        return today.getStreak() != null && today.getStreak() > 0;
    }

    public boolean repairStreak(User user) {
        Sort sort = Sort.by(Sort.Direction.DESC, "date");

        List<LearnTimeStats> stats = getStats(LocalDate.now().minusDays(90), LocalDate.now().minusDays(1), user.getId(), sort);

        Stack<LearnTimeStats> streakStack = new Stack<>();
        LearnTimeStats lastStat;
        boolean brokenFound = false;

        for (LearnTimeStats stat : stats) {

            lastStat = streakStack.isEmpty() ? null : streakStack.peek();

            if (lastStat != null) {
                long daysBetween = Math.abs(ChronoUnit.DAYS.between(stat.getDate(), lastStat.getDate()));

                if (brokenFound) {
                    if (daysBetween == 1) {
                        streakStack.push(stat);
                        break;
                    } else {
                        return false;
                    }
                }

                if (daysBetween > 2) {
                    return false;
                }

                if (daysBetween == 2) {
                    streakStack.push(new LearnTimeStats(user, stat.getDate().plusDays(1), Duration.ZERO, 0));
                    brokenFound = true;
                    streakStack.push(stat);
                    break;
                } else {
                    if (stat.getStreak() == null || stat.getStreak() < 1) {
                        brokenFound = true;
                    }
                }

            }

            streakStack.push(stat);
        }

        if (!brokenFound || streakStack.isEmpty() || streakStack.peek().getStreak() == null || streakStack.peek().getStreak() < 1) {
            return false;
        }

        Set<LearnTimeStats> toUpdate = new HashSet<>();

        lastStat = streakStack.pop();
        LearnTimeStats fixed = streakStack.pop();

        if (Math.abs(ChronoUnit.DAYS.between(lastStat.getDate(), fixed.getDate())) != 1) {
            return false;
        }

        fixed.setStreak(lastStat.getStreak() + 1);
        toUpdate.add(fixed);
        lastStat = fixed;

        while (!streakStack.isEmpty()) {
            LearnTimeStats stat = streakStack.pop();

            stat.setStreak(lastStat.getStreak() + 1);
            toUpdate.add(stat);

            lastStat = stat;
        }

        statsRepository.saveAll(toUpdate);

        return true;
    }
}

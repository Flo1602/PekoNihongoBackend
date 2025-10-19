package at.primetshofer.pekoNihongoBackend.service;

import at.primetshofer.pekoNihongoBackend.dto.ProgressDataDto;
import at.primetshofer.pekoNihongoBackend.dto.japneseLearningApp.OldProgressDto;
import at.primetshofer.pekoNihongoBackend.entity.Learnable;
import at.primetshofer.pekoNihongoBackend.entity.Progress;
import at.primetshofer.pekoNihongoBackend.events.DailyProgressEvent;
import at.primetshofer.pekoNihongoBackend.repository.IProgressRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

@Service
public class TrainerService {

    private final ApplicationEventPublisher eventPublisher;

    public TrainerService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public <T extends Learnable> long getDueElementsCount(IProgressRepository<T> progressRepository, Long userId, int maxElements) {
        if (maxElements <= 0) {
            maxElements = Integer.MAX_VALUE;
        }
        long dueCount = progressRepository.countByUserIdAndProgress_NextDueDateLessThanEqualOrUserIdAndProgress_IsDueTodayOrUserIdAndProgressIsNull(
                userId,
                LocalDate.now(),
                userId,
                true,
                userId);

        return Math.min(maxElements, dueCount);
    }

    public <T extends Learnable> long getCompletedToday(IProgressRepository<T> progressRepository, Long userId) {
        return progressRepository.countByProgress_LastLearnedAndProgress_IsDueTodayAndProgressIsNotNullAndUserId(
                LocalDate.now(),
                false,
                userId,
                Limit.unlimited()
        );
    }

    public <T extends Learnable> List<T> getDueElements(IProgressRepository<T> progressRepository, int elementsToGet, Long userId, int maxElements) {
        long finishedLearning = getCompletedToday(progressRepository, userId);
        int getCount = maxElements - (int) finishedLearning;

        if (getCount <= 0) {
            return List.of();
        }

        Sort sort = JpaSort.unsafe("CASE " +
                        "WHEN progress.isDueToday IS TRUE THEN 0 " +
                        "WHEN progress.isDueToday IS NULL THEN 1 " +
                        "ELSE 2 END")
                .ascending()
                .and(Sort.by(Sort.Order.desc("id")));

        Limit limit = Limit.of(getCount);

        List<T> dueElements = progressRepository.findAllByUserIdAndProgress_NextDueDateLessThanEqualOrUserIdAndProgress_IsDueTodayOrUserIdAndProgressIsNull(
                userId,
                LocalDate.now(),
                userId,
                true,
                userId,
                sort,
                limit);

        Collections.shuffle(dueElements);

        if (elementsToGet > dueElements.size()) {
            return dueElements;
        }

        return dueElements.subList(0, elementsToGet);
    }

    public <T extends Learnable> void saveProgress(T t, IProgressRepository<T> progressRepository, int percentage, Long userId) {
        Progress updatedProgress = t.getProgress();

        if (updatedProgress == null) {
            updatedProgress = new Progress();
            updatedProgress.setFirstLearned(LocalDate.now());
            updatedProgress.setLearnedDays(0);
            updatedProgress.setPenalty(0);
        }

        updatedProgress.setPenalty(calculateNewPenalty(updatedProgress, percentage));
        updatedProgress.setPoints(calculateNewPoints(updatedProgress, percentage));

        int intervalDays = getIntervalDays(updatedProgress.getPoints());
        updatedProgress.setNextDueDate(LocalDate.now().plusDays(intervalDays));

        int maxIntervalDays = getIntervalDays(getDynamicMaxPoints(updatedProgress));
        boolean dueToday = intervalDays < maxIntervalDays;

        updatedProgress.setDueToday(dueToday);

        updatedProgress.setLastLearned(LocalDate.now());

        t.setProgress(updatedProgress);

        progressRepository.save(t);

        if(!dueToday){
            eventPublisher.publishEvent(new DailyProgressEvent(userId, 1, t.getClass()));
        }
    }

    private int calculateNewPenalty(Progress progress, int percentage) {
        int dynamicMaxPoints = getDynamicMaxPoints(progress);
        int penaltyIncrease = getPenaltyIncrease(dynamicMaxPoints, percentage);
        int penalty = progress.getPenalty() + penaltyIncrease;

        if (percentage == 100 && !isToday(progress.getLastLearned())) {
            penalty -= 2 * getPenaltyIncrease(dynamicMaxPoints, 0);
        }

        if (penalty < 0) {
            penalty = 0;
        }
        if (penalty > 90) {
            penalty = 90;
        }

        return penalty;
    }

    private int calculateNewPoints(Progress progress, int percentage) {
        int pointsIncrement = calculatePointsIncrement(progress, percentage);

        pointsIncrement = (int) (pointsIncrement * getPenalty(progress));

        if (!isToday(progress.getLastLearned())) {
            progress.setPoints(0);
            progress.setLearnedDays(progress.getLearnedDays() + 1);
        }

        int newPoints = progress.getPoints() + pointsIncrement;
        int maxPoints = getDynamicMaxPoints(progress);

        if (newPoints > maxPoints) {
            newPoints = maxPoints;
        }

        return newPoints;
    }

    private double getPenalty(Progress progress) {
        return (100.0 - progress.getPenalty()) / 100.0;
    }

    private int calculatePointsIncrement(Progress progress, int percentage) {
        int baseIncrement;
        if (percentage >= 200) {
            baseIncrement = 35;
        } else if (percentage >= 95) {
            baseIncrement = 25;
        } else if (percentage >= 80) {
            baseIncrement = 15;
        } else if (percentage >= 60) {
            baseIncrement = 10;
        } else if (percentage == 0) {
            baseIncrement = 0;
        } else {
            baseIncrement = 5;
        }

        int reviewCount = progress.getLearnedDays();

        long daysSinceFirstLearned = 0;

        if (progress.getFirstLearned() != null) {
            daysSinceFirstLearned = ChronoUnit.DAYS.between(progress.getFirstLearned(), LocalDate.now());
        }

        if (daysSinceFirstLearned < 0) {
            daysSinceFirstLearned = 0;
        }
        daysSinceFirstLearned /= 2;

        double incrementFactor = 1.0 + ((reviewCount + daysSinceFirstLearned) * 0.3);
        if (reviewCount == 0 || reviewCount == 1) {
            incrementFactor = 0.5;
        }
        if (incrementFactor > 20) {
            incrementFactor = 20;
        }
        return (int) Math.round(baseIncrement * incrementFactor);
    }

    private int getDynamicMaxPoints(Progress progress) {
        int reviewCount = progress.getLearnedDays();

        if (progress.getLearnedDays() == 0) {
            return 50;
        }

        long daysSinceFirstLearned = ChronoUnit.DAYS.between(progress.getFirstLearned(), LocalDate.now());
        if (daysSinceFirstLearned < 0) {
            daysSinceFirstLearned = 0;
        }
        daysSinceFirstLearned /= 4;

        int dynamicMax = (int) Math.min((daysSinceFirstLearned + reviewCount) * 20, 600);
        dynamicMax = (int) (dynamicMax * getPenalty(progress));
        if (dynamicMax < 0) {
            dynamicMax = 600;
        }
        if (dynamicMax < 50) {
            dynamicMax = 50;
        }

        return dynamicMax;
    }

    private int getIntervalDays(int points) {
        if (points < 30) {
            return 0;
        } else if (points < 60) {
            return 1;
        } else if (points < 100) {
            return 2;
        } else if (points < 175) {
            return 5;
        } else if (points < 300) {
            return 11;
        } else if (points < 500) {
            return 21;
        } else {
            return 31;
        }
    }

    private int getPenaltyIncrease(int maxPoints, int percentage) {
        if (percentage == 100) {
            return 0;
        }

        int basePenalty;
        if (maxPoints < 60) {
            basePenalty = 5;
        } else if (maxPoints < 100) {
            basePenalty = 7;
        } else if (maxPoints < 175) {
            basePenalty = 10;
        } else if (maxPoints < 300) {
            basePenalty = 20;
        } else if (maxPoints < 500) {
            basePenalty = 30;
        } else {
            basePenalty = 50;
        }

        int penaltyIncrease = (int) Math.round(basePenalty * ((100 - percentage) / 100.0));

        if (penaltyIncrease < 0) {
            penaltyIncrease = 0;
        }

        return penaltyIncrease;
    }

    private boolean isToday(LocalDate date) {
        return date != null && date.isEqual(LocalDateTime.now().toLocalDate());
    }

    public <T extends Learnable> void importOldData(T t, List<OldProgressDto> oldProgresses, IProgressRepository<T> progressRepository) {
        Progress progress = new Progress();
        int learnedDays = 0;
        for (OldProgressDto oldProgress : oldProgresses) {
            learnedDays += oldProgress.compressedEntries();
        }
        progress.setLearnedDays(learnedDays);
        progress.setFirstLearned(oldProgresses.getFirst().learned().toLocalDate());
        progress.setLastLearned(oldProgresses.getLast().learned().toLocalDate());
        progress.setPoints(oldProgresses.getLast().points());
        progress.setDueToday(false);
        progress.setNextDueDate(oldProgresses.getLast().learned().toLocalDate().plusDays(getIntervalDays(oldProgresses.getLast().points())));

        t.setProgress(progress);

        progressRepository.save(t);
    }

    public <T extends Learnable> ProgressDataDto progressDataDto(IProgressRepository<T> progressRepository, Long userId, int maxElements) {
        long dueToday = getDueElementsCount(progressRepository, userId, maxElements);
        long dueTotal = getDueElementsCount(progressRepository, userId, -1);
        long completedToday = getCompletedToday(progressRepository, userId);

        return new ProgressDataDto(dueToday, completedToday, dueTotal);
    }
}

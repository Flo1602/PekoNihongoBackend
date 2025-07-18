package at.primetshofer.pekoNihongoBackend.service;

import at.primetshofer.pekoNihongoBackend.dto.ProgressDataDto;
import at.primetshofer.pekoNihongoBackend.dto.japneseLearningApp.OldProgressDto;
import at.primetshofer.pekoNihongoBackend.entity.Learnable;
import at.primetshofer.pekoNihongoBackend.entity.Progress;
import at.primetshofer.pekoNihongoBackend.entity.User;
import at.primetshofer.pekoNihongoBackend.repository.IProgressRepository;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

@Service
public class TrainerService {

    public <T extends Learnable> long getDueElementsCount(IProgressRepository<T> progressRepository, Long userId, int maxElements){
        if(maxElements <= 0) {
            maxElements = Integer.MAX_VALUE;
        }
        long dueCount = progressRepository.countByProgress_NextDueDateLessThanEqualOrProgress_IsDueTodayOrProgressIsNullAndUserId(LocalDate.now(),
                true,
                userId);

        return Math.min(maxElements, dueCount);
    }

    public <T extends Learnable> long getCompletedToday(IProgressRepository<T> progressRepository, Long userId){
        return progressRepository.countByProgress_LastLearnedAndProgress_IsDueTodayAndProgressIsNotNullAndUserId(
                LocalDate.now(),
                false,
                userId,
                Limit.unlimited()
        );
    }

    public <T extends Learnable> List<T> getDueElements(IProgressRepository<T> progressRepository, int elementsToGet, Long userId, int maxElements){
        long finishedLearning = getCompletedToday(progressRepository, userId);
        int getCount = maxElements - (int)finishedLearning;

        if(getCount <= 0) {
            return List.of();
        }

        Sort.Order orderIsDueTodayDesc = new Sort.Order(Sort.Direction.DESC, "progress.isDueToday").nullsFirst();

        Sort.Order orderIdDesc = new Sort.Order(Sort.Direction.DESC, "id");

        Sort sort = Sort.by(orderIsDueTodayDesc, orderIdDesc);
        Limit limit = Limit.of(getCount);

        List<T> dueElements = progressRepository.findAllByProgress_NextDueDateLessThanEqualOrProgress_IsDueTodayOrProgressIsNullAndUserId(
                LocalDate.now(),
                true,
                sort,
                limit,
                userId);

        Collections.shuffle(dueElements);

        if(elementsToGet > dueElements.size()) {
            return dueElements;
        }

        return dueElements.subList(0, elementsToGet);
    }

    public <T extends Learnable> void saveProgress(T t, IProgressRepository<T> progressRepository, int percentage) {
        Progress updatedProgress = t.getProgress();

        if(updatedProgress == null) {
            updatedProgress = new Progress();
            updatedProgress.setFirstLearned(LocalDate.now());
            updatedProgress.setLearnedDays(0);
        }

        if (!isToday(updatedProgress.getLastLearned())){
            updatedProgress.setPoints(0);
            updatedProgress.setLearnedDays(updatedProgress.getLearnedDays() + 1);
        }

        updatedProgress.setPoints(calculateNewPoints(updatedProgress, percentage));

        int intervalDays = getIntervalDays(updatedProgress.getPoints());
        updatedProgress.setNextDueDate(LocalDate.now().plusDays(intervalDays));

        int maxIntervalDays = getIntervalDays(getDynamicMaxPoints(updatedProgress));

        updatedProgress.setDueToday(intervalDays < maxIntervalDays);

        updatedProgress.setLastLearned(LocalDate.now());

        t.setProgress(updatedProgress);

        progressRepository.save(t);
    }

    private int calculateNewPoints(Progress progress, int percentage) {
        int pointsIncrement = calculatePointsIncrement(progress, percentage);
        int newPoints = progress.getPoints() + pointsIncrement;
        int maxPoints = getDynamicMaxPoints(progress);

        if(newPoints > maxPoints) {
            newPoints = maxPoints;
        }

        return newPoints;
    }

    private int calculatePointsIncrement(Progress progress, int percentage) {
        int baseIncrement;
        if (percentage >= 95) {
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

    public <T extends Learnable> ProgressDataDto ProgressDataDto(IProgressRepository<T> progressRepository, Long userId, int maxElements) {
        long dueToday = getDueElementsCount(progressRepository, userId, maxElements);
        long dueTotal = getDueElementsCount(progressRepository, userId, -1);
        long completedToday = getCompletedToday(progressRepository, userId);

        return new ProgressDataDto(dueToday, completedToday, dueTotal);
    }
}

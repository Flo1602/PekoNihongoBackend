package at.primetshofer.pekoNihongoBackend.repository;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;

public interface IProgressRepository<T> {

    List<T> findAllByUserIdAndProgress_NextDueDateLessThanEqualOrUserIdAndProgress_IsDueTodayOrUserIdAndProgressIsNull(
            Long      userId1,
            LocalDate nextDueDate,
            Long      userId2,
            Boolean   isDueToday,
            Long      userId3,
            Sort      sort,
            Limit  limit);

    T save(T entity);

    long countByUserIdAndProgress_NextDueDateLessThanEqualOrUserIdAndProgress_IsDueTodayOrUserIdAndProgressIsNull(
            Long      userId1,
            LocalDate nextDueDate,
            Long      userId2,
            Boolean   isDueToday,
            Long      userId3
    );

    long countByProgress_LastLearnedAndProgress_IsDueTodayAndProgressIsNotNullAndUserId(
            LocalDate progressNextDueDateIsLessThanEqual,
            Boolean progressIsDueToday,
            Long userId,
            Limit limit
    );
}

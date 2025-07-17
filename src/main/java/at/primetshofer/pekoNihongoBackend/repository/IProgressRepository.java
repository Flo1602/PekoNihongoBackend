package at.primetshofer.pekoNihongoBackend.repository;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;

public interface IProgressRepository<T> {

    List<T> findAllByProgress_NextDueDateLessThanEqualOrProgress_IsDueTodayOrProgressIsNullAndUserId(LocalDate progressNextDueDateIsLessThan,
                                                                                                     Boolean progressIsDueToday,
                                                                                                     Sort sort,
                                                                                                     Limit limit,
                                                                                                     Long userId);

    T save(T entity);

    long countByProgress_NextDueDateLessThanEqualOrProgress_IsDueTodayOrProgressIsNullAndUserId(
            LocalDate progressNextDueDateIsLessThanEqual,
            Boolean progressIsDueToday,
            Long userId
    );

    long countByProgress_LastLearnedAndProgress_IsDueTodayAndProgressIsNotNullAndUserId(
            LocalDate progressNextDueDateIsLessThanEqual,
            Boolean progressIsDueToday,
            Long userId,
            Limit limit
    );
}

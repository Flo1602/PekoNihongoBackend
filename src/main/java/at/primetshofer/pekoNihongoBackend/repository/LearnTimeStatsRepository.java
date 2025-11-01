package at.primetshofer.pekoNihongoBackend.repository;

import at.primetshofer.pekoNihongoBackend.entity.LearnTimeStats;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LearnTimeStatsRepository extends JpaRepository<LearnTimeStats, Long> {
    LearnTimeStats findByUserIdAndDate(Long userId, LocalDate date);

    @Query(value = "SELECT COALESCE(SUM(duration), 0) FROM learn_time_stats WHERE user_id = :userId", nativeQuery = true)
    Long sumDurationNanos(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(l.exercises), 0) FROM LearnTimeStats l WHERE l.user.id = :userId")
    Integer sumExercises(@Param("userId") Long userId);

    List<LearnTimeStats> findByUserId(Long userId, Sort sort, Limit limit);

    List<LearnTimeStats> findByUserIdAndDateBetween(Long userId, LocalDate dateAfter, LocalDate dateBefore);

    List<LearnTimeStats> findByUserIdAndDateBetween(Long userId, LocalDate dateAfter, LocalDate dateBefore, Sort sort);
}

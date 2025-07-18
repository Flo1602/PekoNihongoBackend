package at.primetshofer.pekoNihongoBackend.repository;

import at.primetshofer.pekoNihongoBackend.entity.LearnTimeStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface LearnTimeStatsRepository extends JpaRepository<LearnTimeStats, Long> {
    LearnTimeStats findByUserIdAndDate(Long userId, LocalDate date);
}

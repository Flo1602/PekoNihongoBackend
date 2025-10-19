package at.primetshofer.pekoNihongoBackend.repository;

import at.primetshofer.pekoNihongoBackend.entity.Quest;
import at.primetshofer.pekoNihongoBackend.enums.QuestCategory;
import at.primetshofer.pekoNihongoBackend.enums.QuestType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface QuestRepository extends JpaRepository<Quest, Long> {

    Quest findByIdAndUserId(Long id, Long userId);

    List<Quest> findByUserIdAndCategory(Long userId, QuestCategory category);

    List<Quest> findByUserIdAndType(Long userId, QuestType type);

    List<Quest> findByUserId(Long userId);

    List<Quest> findByUserIdAndExpirationDateBefore(Long userId, LocalDate expirationDateBefore);
}

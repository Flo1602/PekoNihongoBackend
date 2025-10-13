package at.primetshofer.pekoNihongoBackend.repository;

import at.primetshofer.pekoNihongoBackend.entity.Quest;
import at.primetshofer.pekoNihongoBackend.entity.QuestCategory;
import at.primetshofer.pekoNihongoBackend.entity.QuestType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestRepository extends JpaRepository<Quest, Long> {

    Quest findByIdAndUserId(Long id, Long userId);

    List<Quest> findByUserIdAndCategory(Long userId, QuestCategory category);

    List<Quest> findByUserIdAndType(Long userId, QuestType type);
}

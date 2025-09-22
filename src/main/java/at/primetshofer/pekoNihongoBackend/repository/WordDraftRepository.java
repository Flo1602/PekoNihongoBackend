package at.primetshofer.pekoNihongoBackend.repository;

import at.primetshofer.pekoNihongoBackend.entity.WordDraft;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WordDraftRepository extends JpaRepository<WordDraft, Long> {
    Optional<WordDraft> findByIdAndUserId(Long wordDraftId, Long userId);

    Page<WordDraft> findAllByUserId(Long userId, Pageable pageable);
}

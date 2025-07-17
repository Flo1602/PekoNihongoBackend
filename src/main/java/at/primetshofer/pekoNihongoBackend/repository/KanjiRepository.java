package at.primetshofer.pekoNihongoBackend.repository;

import at.primetshofer.pekoNihongoBackend.entity.Kanji;
import at.primetshofer.pekoNihongoBackend.entity.Word;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface KanjiRepository extends JpaRepository<Kanji, Long> {
    List<Kanji> getAllBySymbolIsAndUserId(char symbol, Long userId);

    @EntityGraph(attributePaths = "words")
    Page<Kanji> findAllByUserId(Long userId, Pageable pageable);

    Optional<Kanji> findByIdAndUserId(Long id, Long userId);

    Kanji findByUserIdAndSymbol(Long userId, char symbol);
}

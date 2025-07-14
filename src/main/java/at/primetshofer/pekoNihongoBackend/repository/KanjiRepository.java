package at.primetshofer.pekoNihongoBackend.repository;

import at.primetshofer.pekoNihongoBackend.entity.Kanji;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KanjiRepository extends JpaRepository<Kanji, Long> {
    List<Kanji> getAllBySymbolIsAndUserId(char symbol, Long userId);
}

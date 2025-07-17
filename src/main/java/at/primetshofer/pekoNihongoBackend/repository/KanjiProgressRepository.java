package at.primetshofer.pekoNihongoBackend.repository;

import at.primetshofer.pekoNihongoBackend.entity.Kanji;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KanjiProgressRepository extends JpaRepository<Kanji, Long>, IProgressRepository<Kanji>{



}

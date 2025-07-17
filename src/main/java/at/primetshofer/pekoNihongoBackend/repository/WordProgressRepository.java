package at.primetshofer.pekoNihongoBackend.repository;

import at.primetshofer.pekoNihongoBackend.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordProgressRepository extends JpaRepository<Word, Long>, IProgressRepository<Word> {



}

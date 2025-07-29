package at.primetshofer.pekoNihongoBackend.repository;

import at.primetshofer.pekoNihongoBackend.entity.Word;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface WordRepository extends JpaRepository<Word, Long> {
    Page<Word> findAllByUserId(Long userId, Pageable pageable);

    Optional<Word> findByIdAndUserId(Long id, Long userId);

    List<Word> getWordsByUserId(Long userId, Limit limit);

    List<Word> getWordsByUserIdAndIdIn(Long userId, Collection<Long> ids);

    @Query("SELECT w FROM Word w WHERE w.user.id = :userId AND w.kanjis IS NOT EMPTY ORDER BY function('RAND')")
    List<Word> findRandomItemsWithKanji(@Param("userId") Long userId, Limit limit);

    @Query("""
        SELECT w FROM Word w
        WHERE w.user.id = :userId
          AND (
            LOWER(w.japanese) LIKE :japanese
            OR LOWER(w.kana) LIKE :kana
            OR LOWER(w.english) LIKE :english
          )
        """)
    Page<Word> searchByUserIdAndFields(
            @Param("userId") Long userId,
            @Param("japanese") String japanese,
            @Param("kana") String kana,
            @Param("english") String english,
            Pageable pageable
    );
}

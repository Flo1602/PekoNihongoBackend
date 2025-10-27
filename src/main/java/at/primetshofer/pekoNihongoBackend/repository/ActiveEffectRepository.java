package at.primetshofer.pekoNihongoBackend.repository;

import at.primetshofer.pekoNihongoBackend.entity.ActiveEffect;
import at.primetshofer.pekoNihongoBackend.enums.EffectType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActiveEffectRepository extends JpaRepository<ActiveEffect, Long> {
    List<ActiveEffect> findByUserIdAndType(Long userId, EffectType type);
}

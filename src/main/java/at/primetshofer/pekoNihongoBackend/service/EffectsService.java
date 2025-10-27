package at.primetshofer.pekoNihongoBackend.service;

import at.primetshofer.pekoNihongoBackend.entity.ActiveEffect;
import at.primetshofer.pekoNihongoBackend.entity.User;
import at.primetshofer.pekoNihongoBackend.enums.EffectType;
import at.primetshofer.pekoNihongoBackend.repository.ActiveEffectRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EffectsService {

    private final ActiveEffectRepository activeEffectRepository;

    public EffectsService(ActiveEffectRepository activeEffectRepository){
        this.activeEffectRepository = activeEffectRepository;
    }

    public boolean applyEffect(User user, EffectType effectType, Duration duration) {
        List<ActiveEffect> activeEffects = activeEffectRepository.findByUserIdAndType(user.getId(), effectType);

        if(!activeEffects.isEmpty()){
            return false;
        }

        ActiveEffect effect = new ActiveEffect();
        effect.setType(effectType);
        effect.setExpirationDateTime(LocalDateTime.now().plus(duration));
        effect.setUser(user);

        activeEffectRepository.save(effect);

        return true;
    }

    public ActiveEffect getActiveEffect(Long userId, EffectType effectType){
        List<ActiveEffect> activeEffects = activeEffectRepository.findByUserIdAndType(userId, effectType);

        if(activeEffects.isEmpty()){
            return null;
        }

        ActiveEffect activeEffect = activeEffects.getFirst();

        if(activeEffect.getExpirationDateTime().isBefore(LocalDateTime.now())){
            activeEffectRepository.delete(activeEffect);
            return null;
        }

        return activeEffect;
    }

    public boolean hasEffect(Long userId, EffectType effectType){
       return getActiveEffect(userId, effectType) != null;
    }
}

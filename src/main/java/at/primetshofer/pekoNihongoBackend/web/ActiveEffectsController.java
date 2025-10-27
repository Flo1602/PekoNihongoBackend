package at.primetshofer.pekoNihongoBackend.web;

import at.primetshofer.pekoNihongoBackend.enums.EffectType;
import at.primetshofer.pekoNihongoBackend.security.authentication.AuthConstants;
import at.primetshofer.pekoNihongoBackend.service.EffectsService;
import at.primetshofer.pekoNihongoBackend.utils.WebUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/effects")
@SecurityRequirement(name = AuthConstants.SECURITY_SCHEME_NAME)
public class ActiveEffectsController {

    private final EffectsService effectsService;
    private final WebUtils webUtils;

    public ActiveEffectsController(EffectsService effectsService, WebUtils webUtils) {
        this.effectsService = effectsService;
        this.webUtils = webUtils;
    }

    @GetMapping("/isActive/{effectType}")
    public boolean getDailyQuests(@PathVariable EffectType effectType){
        return effectsService.hasEffect(webUtils.getCurrentUserId(), effectType);
    }

}

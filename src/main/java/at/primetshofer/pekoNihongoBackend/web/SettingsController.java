package at.primetshofer.pekoNihongoBackend.web;

import at.primetshofer.pekoNihongoBackend.dto.SettingsDto;
import at.primetshofer.pekoNihongoBackend.entity.UserSettings;
import at.primetshofer.pekoNihongoBackend.security.authentication.AuthConstants;
import at.primetshofer.pekoNihongoBackend.service.UserService;
import at.primetshofer.pekoNihongoBackend.utils.WebUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
@SecurityRequirement(name = AuthConstants.SECURITY_SCHEME_NAME)
public class SettingsController {

    private final WebUtils webUtils;
    private final UserService userService;

    public SettingsController(WebUtils webUtils, UserService userService) {
        this.webUtils = webUtils;
        this.userService = userService;
    }

    @GetMapping
    public SettingsDto get() {
        UserSettings userSettings = webUtils.getCurrentUser().getUserSettings();
        if(userSettings != null) {
            return new SettingsDto(userSettings);
        }
        return new SettingsDto(0, 0, 0, false);
    }

    @PutMapping
    public boolean update(@RequestBody SettingsDto settingsDto) {
        userService.updateUserSettings(webUtils.getCurrentUserId(),
                new UserSettings(settingsDto.voiceId(), settingsDto.maxDailyWords(), settingsDto.maxDailyKanji(), settingsDto.useAlwaysVoiceVox()));

        return true;
    }
}

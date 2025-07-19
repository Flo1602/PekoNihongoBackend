package at.primetshofer.pekoNihongoBackend.dto;

import at.primetshofer.pekoNihongoBackend.entity.UserSettings;

public record SettingsDto(Integer voiceId, Integer maxDailyWords, Integer maxDailyKanji, Boolean useAlwaysVoiceVox) {
    public SettingsDto(UserSettings userSettings) {
        this(userSettings.getVoiceId(), userSettings.getMaxDailyWords(), userSettings.getMaxDailyKanji(), userSettings.getUseAlwaysVoiceVox());
    }
}

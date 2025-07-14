package at.primetshofer.pekoNihongoBackend.dto;

import at.primetshofer.pekoNihongoBackend.entity.UserSettings;

public record SettingsDto(Integer voiceId, Integer maxDailyWords, Integer maxDailyKanji) {
    public SettingsDto(UserSettings userSettings) {
        this(userSettings.getVoiceId(), userSettings.getMaxDailyWords(), userSettings.getMaxDailyKanji());
    }
}

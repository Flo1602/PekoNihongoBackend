package at.primetshofer.pekoNihongoBackend.entity;

import jakarta.persistence.*;

@Embeddable
public class UserSettings {

    private Integer voiceId;

    private Integer maxDailyWords;

    private Integer maxDailyKanji;

    public Integer getVoiceId() {
        return voiceId;
    }

    public void setVoiceId(Integer voiceId) {
        this.voiceId = voiceId;
    }

    public Integer getMaxDailyWords() {
        return maxDailyWords;
    }

    public void setMaxDailyWords(Integer maxDailyWords) {
        this.maxDailyWords = maxDailyWords;
    }

    public Integer getMaxDailyKanji() {
        return maxDailyKanji;
    }

    public void setMaxDailyKanji(Integer maxDailyKanji) {
        this.maxDailyKanji = maxDailyKanji;
    }
}

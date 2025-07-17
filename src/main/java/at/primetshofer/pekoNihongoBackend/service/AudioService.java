package at.primetshofer.pekoNihongoBackend.service;

import at.primetshofer.pekoNihongoBackend.config.WebMvcConfig;
import at.primetshofer.pekoNihongoBackend.entity.User;
import at.primetshofer.pekoNihongoBackend.entity.UserSettings;
import at.primetshofer.pekoNihongoBackend.entity.Word;
import at.primetshofer.pekoNihongoBackend.repository.UserRepository;
import at.primetshofer.pekoNihongoBackend.repository.WordRepository;
import at.primetshofer.pekoNihongoBackend.utils.JishoAudioFetcher;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class AudioService {

    private final UserRepository userRepository;
    private final TTSService ttsService;
    private final AsyncService asyncService;
    private final WordRepository wordRepository;

    public AudioService(UserRepository userRepository, TTSService ttsService, AsyncService asyncService, WordRepository wordRepository) {
        this.userRepository = userRepository;
        this.ttsService = ttsService;
        this.asyncService = asyncService;
        this.wordRepository = wordRepository;
    }

    public void addAudioToWord(Word word) {
        asyncService.runAsync(() -> {
            try {
                File audioFile = JishoAudioFetcher.fetchAudioURL(word.getJapanese(), word.getId());

                if (audioFile == null) {
                    UserSettings settings = userRepository.findById(word.getUser().getId()).orElse(new User()).getUserSettings();
                    if (settings == null) {
                        settings = new UserSettings();
                    }

                    String ttsString = (word.getKana() == null) ? word.getJapanese() : word.getKana();
                    audioFile = ttsService.synthesizeAudio(ttsString, TTSService.WORD_AUDIO_SAVE_PATH + "/" + word.getId() + ".wav", settings.getVoiceId());
                }

                word.setTtsPath(audioFile.getPath().replace("\\", "/").split("audio/")[1]);

                wordRepository.save(word);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public void deleteAudioFile(Word word){
        if (word.getTtsPath() != null) {
            try {
                File audioFile = new java.io.File((WebMvcConfig.STATIC_RESOURCE_LOCATION + "/audio/" + word.getTtsPath()));
                boolean delete = audioFile.delete();
                if (!delete) {
                    throw new Exception("Could not delete audio file " + audioFile.getPath());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

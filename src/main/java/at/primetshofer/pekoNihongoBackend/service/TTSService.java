package at.primetshofer.pekoNihongoBackend.service;

import at.primetshofer.pekoNihongoBackend.config.WebMvcConfig;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
public class TTSService {

    public static final String WORD_AUDIO_SAVE_PATH = WebMvcConfig.AUDIO_PATH + "/words";

    private static final String VOICEVOX_BASE_URL = "http://localhost:50021"; // Default Voicevox API URL

    public File synthesizeAudio(String text, String savePath, Integer speakerId) throws IOException, InterruptedException {
        return synthesizeAudio(text, savePath, 1.0, speakerId);
    }

    public File synthesizeAudio(String text, String savePath, double speedScale, Integer speakerId) throws IOException, InterruptedException {
        if(speakerId == null) {
            speakerId = 0;
        }
        HttpClient client = HttpClient.newHttpClient();

        // Step 1: Generate audio query
        String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);
        URI queryUri = URI.create(VOICEVOX_BASE_URL + "/audio_query?text=" + encodedText + "&speaker=" + speakerId);
        HttpRequest queryRequest = HttpRequest.newBuilder()
                .uri(queryUri)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> queryResponse = client.send(queryRequest, HttpResponse.BodyHandlers.ofString());
        if (queryResponse.statusCode() != 200) {
            throw new RuntimeException("Failed to get audio query: " + queryResponse.body());
        }

        // Step 1.5: Modify the audio query JSON to slow down speech.
        // Here we set the "speedScale" parameter to 0.7 for slower speech.
        JSONObject jsonQuery = new JSONObject(queryResponse.body());
        jsonQuery.put("speedScale", speedScale);  // Adjust the value as needed for slower speed.
        String modifiedAudioQuery = jsonQuery.toString();

        // Step 2: Synthesize audio using the generated query
        URI synthesisUri = URI.create(VOICEVOX_BASE_URL + "/synthesis?speaker=" + speakerId);
        HttpRequest synthesisRequest = HttpRequest.newBuilder()
                .uri(synthesisUri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(modifiedAudioQuery))
                .build();

        HttpResponse<InputStream> synthesisResponse = client.send(synthesisRequest, HttpResponse.BodyHandlers.ofInputStream());
        if (synthesisResponse.statusCode() != 200) {
            throw new RuntimeException("Failed to synthesize audio: " + synthesisResponse.body());
        }

        // Step 3: Save the audio to a .wav file

        File tmpFile = new File(savePath);

        if (!tmpFile.getParentFile().exists()) {
            tmpFile.getParentFile().mkdirs();
        }
        if (!tmpFile.exists()) {
            tmpFile.createNewFile();
        }

        try (InputStream inputStream = synthesisResponse.body(); OutputStream outputStream = new FileOutputStream(tmpFile)) {
            inputStream.transferTo(outputStream);
            return tmpFile;
        }
    }

}

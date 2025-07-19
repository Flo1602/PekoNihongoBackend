package at.primetshofer.pekoNihongoBackend.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class JishoAudioFetcher {
    @Value("${pekoNihongoBackend.resources.location}")
    private String staticResourceLocation;
    @Value("${pekoNihongoBackend.resources.wordAudioSavePath}")
    private String wordAudioSavePath;

    private static final String JISHO_URL = "https://jisho.org/search/";

    public File fetchAudioURL(String japanese, Long id) {
        String searchUrl = JISHO_URL + japanese;

        try {
            Document doc = Jsoup.connect(searchUrl).get();

            for (Element audioElement : doc.select("audio")) {
                String audioId = audioElement.id();
                audioId = audioId.replaceAll("audio_", "");
                audioId = audioId.split(":")[0];

                if (audioId.equals(japanese)) { // Ensure the audio file is related to the word
                    Element sourceElement = audioElement.selectFirst("source");
                    if (sourceElement != null) {
                        String audioUrl = sourceElement.attr("src");
                        return downloadAudio(audioUrl, id);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            //logger.error("Failed to fetch Jisho audio URL '" + searchUrl + "'", ex);
        }
        return null;
    }

    private File downloadAudio(String audioUrl, Long id) {
        try {
            URL url = new URL("https:" + audioUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            File audioFile = new File(staticResourceLocation + wordAudioSavePath + "/" + id + ".mp3");
            try (InputStream in = connection.getInputStream(); FileOutputStream out = new FileOutputStream(audioFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            return audioFile;
        } catch (Exception ex) {
            ex.printStackTrace();
            //logger.error("Failed to download Jisho audio file from URL '" + audioUrl + "'", ex);
        }

        return null;
    }
}

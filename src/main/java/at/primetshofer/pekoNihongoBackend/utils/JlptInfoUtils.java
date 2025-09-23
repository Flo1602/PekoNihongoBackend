package at.primetshofer.pekoNihongoBackend.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class JlptInfoUtils {

    public static String fetchWordJlpt(HttpClient http, ObjectMapper mapper, String word) {
        try {
            String url = "https://jlpt-vocab-api.vercel.app/api/words?word=" + urlEnc(word);
            HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();
            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() != 200 || res.body() == null || res.body().isBlank()) return null;

            JsonNode root = mapper.readTree(res.body());

            // REST shape appears to be: { total: number, words: [{ word, meaning, furigana, level }] }
            JsonNode wordsNode = root.has("words") ? root.get("words") : root; // fallback if it ever returns an array
            if (wordsNode == null || !wordsNode.isArray() || wordsNode.isEmpty()) return null;

            // Prefer exact match; otherwise take the first entry
            JsonNode best = null;
            for (JsonNode n : wordsNode) {
                if (n.hasNonNull("word") && word.equals(n.get("word").asText())) {
                    best = n;
                    break;
                }
            }
            if (best == null) best = wordsNode.get(0);

            if (best.has("level") && best.get("level").canConvertToInt()) {
                int lvl = best.get("level").asInt(); // 1..5 (N1..N5)
                return "JLPT N" + lvl;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static String fetchKanjiJlpt(HttpClient http, ObjectMapper mapper, String kanji) {
        try {
            String url = "https://kanjiapi.dev/v1/kanji/" + urlEnc(kanji);
            HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();
            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() != 200 || res.body() == null || res.body().isBlank()) return null;

            JsonNode node = mapper.readTree(res.body());
            if (node.has("jlpt") && node.get("jlpt").canConvertToInt()) {
                int lvl = node.get("jlpt").asInt(); // 1..5 → N1..N5
                return "JLPT N" + lvl;
            }
            return null; // kanji may not have a JLPT tag
        } catch (Exception e) {
            return null;
        }
    }

    private static List<String> extractKanjiChars(String text) {
        // Unique kanji, in order of first appearance
        return text.codePoints()
                .mapToObj(cp -> new String(Character.toChars(cp)))
                .filter(JlptInfoUtils::isKanji)
                .distinct()
                .collect(Collectors.toList());
    }

    private static boolean isKanji(String ch) {
        int cp = ch.codePointAt(0);
        Character.UnicodeBlock block = Character.UnicodeBlock.of(cp);
        // Core CJK blocks (basic + extensions); adjust if you want to include compatibility, etc.
        return block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C
                || block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D
                || block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_E
                || block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_F
                || block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_G;
    }

    private static String urlEnc(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}

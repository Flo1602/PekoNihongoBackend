package at.primetshofer.pekoNihongoBackend.utils;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;

import java.util.ArrayList;
import java.util.List;

public class JapaneseUtils {

    public static List<Character> extractKanji(String text) {
        List<Character> kanjiList = new ArrayList<>();

        // Regular expression for Kanji characters (Unicode range for Kanji)
        String kanjiRegex = "[\\p{InCJK_Unified_Ideographs}]";

        // Traverse each character in the input text
        for (char c : text.toCharArray()) {
            // If the character matches the Kanji regex, add it to the list
            if (String.valueOf(c).matches(kanjiRegex)) {
                kanjiList.add(c);
            }
        }
        return kanjiList;
    }

    public static String convertKanjiToKatakana(String text) {
        Tokenizer tokenizer = new Tokenizer();
        StringBuilder hiraganaResult = new StringBuilder();

        for (Token token : tokenizer.tokenize(text)) {
            String reading = token.getReading();
            if (reading != null) {
                // Convert Katakana reading to Hiragana
                StringBuilder katakana = new StringBuilder();
                for (char c : reading.toCharArray()) {
                    katakana.append(c);
                }
                hiraganaResult.append(katakana);
            } else {
                // If no reading is available, use the surface form
                hiraganaResult.append(token.getSurface());
            }
        }

        return hiraganaResult.toString();
    }
}

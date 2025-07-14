package at.primetshofer.pekoNihongoBackend.utils;

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

}

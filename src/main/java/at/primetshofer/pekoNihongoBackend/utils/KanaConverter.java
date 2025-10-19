package at.primetshofer.pekoNihongoBackend.utils;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;

public class KanaConverter {

    private KanaConverter() {}

    // Katakana to Romaji map
    private static final Map<String, String> KATAKANA_TO_ROMAJI = new HashMap<>();
    // Hiragana to Romaji map
    private static final Map<String, String> HIRAGANA_TO_ROMAJI = new HashMap<>();

    static {
        // Populate katakana map
        String[] katakana = {
                "ア", "イ", "ウ", "エ", "オ",
                "カ", "キ", "ク", "ケ", "コ",
                "サ", "シ", "ス", "セ", "ソ",
                "タ", "チ", "ツ", "テ", "ト",
                "ナ", "ニ", "ヌ", "ネ", "ノ",
                "ハ", "ヒ", "フ", "ヘ", "ホ",
                "マ", "ミ", "ム", "メ", "モ",
                "ヤ", "ユ", "ヨ",
                "ラ", "リ", "ル", "レ", "ロ",
                "ワ", "ヲ", "ン",
                "ガ", "ギ", "グ", "ゲ", "ゴ",
                "ザ", "ジ", "ズ", "ゼ", "ゾ",
                "ダ", "ヂ", "ヅ", "デ", "ド",
                "バ", "ビ", "ブ", "ベ", "ボ",
                "パ", "ピ", "プ", "ペ", "ポ",
                "キャ", "キュ", "キョ",
                "シャ", "シュ", "ショ",
                "チャ", "チュ", "チョ",
                "ニャ", "ニュ", "ニョ",
                "ヒャ", "ヒュ", "ヒョ",
                "ミャ", "ミュ", "ミョ",
                "リャ", "リュ", "リョ",
                "ギャ", "ギュ", "ギョ",
                "ジャ", "ジュ", "ジョ",
                "ビャ", "ビュ", "ビョ",
                "ピャ", "ピュ", "ピョ"
        };
        String[] romajiKatakana = {
                "a", "i", "u", "e", "o",
                "ka", "ki", "ku", "ke", "ko",
                "sa", "shi", "su", "se", "so",
                "ta", "chi", "tsu", "te", "to",
                "na", "ni", "nu", "ne", "no",
                "ha", "hi", "fu", "he", "ho",
                "ma", "mi", "mu", "me", "mo",
                "ya", "yu", "yo",
                "ra", "ri", "ru", "re", "ro",
                "wa", "wo", "n",
                "ga", "gi", "gu", "ge", "go",
                "za", "ji", "zu", "ze", "zo",
                "da", "ji", "zu", "de", "do",
                "ba", "bi", "bu", "be", "bo",
                "pa", "pi", "pu", "pe", "po",
                "kya", "kyu", "kyo",
                "sha", "shu", "sho",
                "cha", "chu", "cho",
                "nya", "nyu", "nyo",
                "hya", "hyu", "hyo",
                "mya", "myu", "myo",
                "rya", "ryu", "ryo",
                "gya", "gyu", "gyo",
                "ja", "ju", "jo",
                "bya", "byu", "byo",
                "pya", "pyu", "pyo"
        };
        for (int i = 0; i < katakana.length; i++) {
            KATAKANA_TO_ROMAJI.put(katakana[i], romajiKatakana[i]);
        }

        // Populate hiragana map
        String[] hiragana = {
                "あ", "い", "う", "え", "お",
                "か", "き", "く", "け", "こ",
                "さ", "し", "す", "せ", "そ",
                "た", "ち", "つ", "て", "と",
                "な", "に", "ぬ", "ね", "の",
                "は", "ひ", "ふ", "へ", "ほ",
                "ま", "み", "む", "め", "も",
                "や", "ゆ", "よ",
                "ら", "り", "る", "れ", "ろ",
                "わ", "を", "ん",
                "が", "ぎ", "ぐ", "げ", "ご",
                "ざ", "じ", "ず", "ぜ", "ぞ",
                "だ", "ぢ", "づ", "で", "ど",
                "ば", "び", "ぶ", "べ", "ぼ",
                "ぱ", "ぴ", "ぷ", "ぺ", "ぽ",
                "きゃ", "きゅ", "きょ",
                "しゃ", "しゅ", "しょ",
                "ちゃ", "ちゅ", "ちょ",
                "にゃ", "にゅ", "にょ",
                "ひゃ", "ひゅ", "ひょ",
                "みゃ", "みゅ", "みょ",
                "りゃ", "りゅ", "りょ",
                "ぎゃ", "ぎゅ", "ぎょ",
                "じゃ", "じゅ", "じょ",
                "びゃ", "びゅ", "びょ",
                "ぴゃ", "ぴゅ", "ぴょ"
        };
        String[] romajiHiragana = romajiKatakana; // Same romaji values for hiragana
        for (int i = 0; i < hiragana.length; i++) {
            HIRAGANA_TO_ROMAJI.put(hiragana[i], romajiHiragana[i]);
        }
    }

    // Method to convert Katakana to Romaji
    public static String katakanaToRomaji(String katakana) {
        return convertKanaToRomaji(katakana, KATAKANA_TO_ROMAJI);
    }

    // Method to convert Hiragana to Romaji
    public static String hiraganaToRomaji(String hiragana) {
        return convertKanaToRomaji(hiragana, HIRAGANA_TO_ROMAJI);
    }

    // Helper method to convert Kana to Romaji
    private static String convertKanaToRomaji(String kana, Map<String, String> kanaToRomajiMap) {
        StringBuilder romaji = new StringBuilder();
        for (int i = 0; i < kana.length(); i++) {
            String currentChar = kana.substring(i, i + 1);

            // Handle compound kana (e.g., きゃ, きゅ, きょ)
            if (i + 1 < kana.length()) {
                String nextChar = kana.substring(i, i + 2);
                if (kanaToRomajiMap.containsKey(nextChar)) {
                    romaji.append(kanaToRomajiMap.get(nextChar));
                    i++; // Skip the next character
                    continue;
                }
            }

            // Handle small tsu (っ or ッ)
            if (currentChar.equals("っ") || currentChar.equals("ッ")) {
                if (i + 1 < kana.length()) {
                    String nextChar = kana.substring(i + 1, i + 2);
                    String mapped = kanaToRomajiMap.get(nextChar);
                    if (mapped != null && !mapped.isEmpty()) {
                        romaji.append(mapped.charAt(0)); // Add the first consonant doubled
                    }
                }
                continue; // Skip processing the small tsu further
            }

            // Regular kana
            romaji.append(kanaToRomajiMap.getOrDefault(currentChar, currentChar));
        }
        return romaji.toString();
    }

    public static String katakanaToHiragana(String input) {
        if (input == null || input.isEmpty()) return input;

        // Normalize: converts halfwidth katakana to fullwidth, composes characters
        String s = Normalizer.normalize(input, Normalizer.Form.NFKC);
        StringBuilder out = new StringBuilder(s.length());

        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);

            // Basic Katakana block that maps cleanly by -0x60
            if (ch >= '\u30A1' && ch <= '\u30F6') {
                out.append((char) (ch - 0x60));
                continue;
            }

            // Iteration marks ヽ (30FD), ヾ (30FE) → ゝ (309D), ゞ (309E)
            if (ch == '\u30FD' || ch == '\u30FE') {
                out.append((char) (ch - 0x60));
                continue;
            }

            // Rare VA/VI/VE/VO letters: ヷ/ヸ/ヹ/ヺ → わ゙/ゐ゙/ゑ゙/を゙ (base + combining dakuten)
            if (ch == '\u30F7') { out.append("わ\u3099"); continue; }
            if (ch == '\u30F8') { out.append("ゐ\u3099"); continue; }
            if (ch == '\u30F9') { out.append("ゑ\u3099"); continue; }
            if (ch == '\u30FA') { out.append("を\u3099"); continue; }

            // Long vowel mark: keep as is (commonly used in kana loanwords)
            if (ch == '\u30FC') { out.append(ch); continue; }

            // Everything else: leave unchanged
            out.append(ch);
        }
        return out.toString();
    }

    /**
     * Convert Hiragana in the input to Katakana.
     * Leaves other characters unchanged. Provided for symmetry.
     */
    public static String hiraganaToKatakana(String input) {
        if (input == null || input.isEmpty()) return input;

        String s = Normalizer.normalize(input, Normalizer.Form.NFKC);
        StringBuilder out = new StringBuilder(s.length());

        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);

            // Basic Hiragana block that maps cleanly by +0x60
            if (ch >= '\u3041' && ch <= '\u3096') {
                out.append((char) (ch + 0x60));
                continue;
            }

            // Iteration marks ゝ (309D), ゞ (309E) → ヽ (30FD), ヾ (30FE)
            if (ch == '\u309D' || ch == '\u309E') {
                out.append((char) (ch + 0x60));
                continue;
            }

            // Combining dakuten/handakuten on hiragana base will carry over visually after normalization,
            // and long vowel mark stays as is.
            out.append(ch);
        }
        return out.toString();
    }
}

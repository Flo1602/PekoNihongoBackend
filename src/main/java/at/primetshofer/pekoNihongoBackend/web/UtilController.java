package at.primetshofer.pekoNihongoBackend.web;

import at.primetshofer.pekoNihongoBackend.entity.Word;
import at.primetshofer.pekoNihongoBackend.security.authentication.AuthConstants;
import at.primetshofer.pekoNihongoBackend.utils.JapaneseUtils;
import at.primetshofer.pekoNihongoBackend.utils.KanaToRomajiConverter;
import at.primetshofer.pekoNihongoBackend.utils.StringSimilarity;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/utils")
@SecurityRequirement(name = AuthConstants.SECURITY_SCHEME_NAME)
public class UtilController {

    private static final Logger logger = LoggerFactory.getLogger(UtilController.class);

    @GetMapping("/japaneseSimilarity")
    public Integer getJapaneseSimilarity(@RequestParam String word, @RequestParam String wordKana) {
        wordKana = wordKana.trim();
        word = word.trim().replace("これは", "").replace("です", "");
        String romaji = KanaToRomajiConverter.katakanaToRomaji(JapaneseUtils.convertKanjiToKatakana(word));
        if (romaji.isEmpty() || romaji.equals("*")) {
            romaji = KanaToRomajiConverter.katakanaToRomaji(word);
        }

        String romajiFromHiragana = KanaToRomajiConverter.hiraganaToRomaji(wordKana);
        String romajiFromKatakana = KanaToRomajiConverter.katakanaToRomaji(wordKana);

        double similarity1 = StringSimilarity.calculateSimilarity(romaji, romajiFromHiragana);
        double similarity2 = StringSimilarity.calculateSimilarity(romaji, romajiFromKatakana);
        double similarity3 = StringSimilarity.calculateSimilarity(word, wordKana);

        logger.info("Similarity 1: " + similarity1 + ", Similarity 2: " + similarity2 + ", Similarity 3: " + similarity3);

        if(similarity1 > similarity2 && similarity1 > similarity3) {
            return (int) (similarity1);
        } else if(similarity2 > similarity1 && similarity2 > similarity3) {
            return (int) (similarity2);
        } else {
            return (int) (similarity3);
        }
    }
}

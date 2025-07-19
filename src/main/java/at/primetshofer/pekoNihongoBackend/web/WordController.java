package at.primetshofer.pekoNihongoBackend.web;

import at.primetshofer.pekoNihongoBackend.dto.PageDto;
import at.primetshofer.pekoNihongoBackend.dto.WordDto;
import at.primetshofer.pekoNihongoBackend.entity.User;
import at.primetshofer.pekoNihongoBackend.entity.Word;
import at.primetshofer.pekoNihongoBackend.security.authentication.AuthConstants;
import at.primetshofer.pekoNihongoBackend.service.WordService;
import at.primetshofer.pekoNihongoBackend.utils.WebUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/words")
@SecurityRequirement(name = AuthConstants.SECURITY_SCHEME_NAME)
public class WordController {

    private final WordService wordService;
    private final WebUtils webUtils;

    public WordController(WordService wordService, WebUtils webUtils) {
        this.wordService = wordService;
        this.webUtils = webUtils;
    }

    @GetMapping
    public PageDto<WordDto> get(@RequestParam int pageSize, @RequestParam int page) {
        Page<Word> wordPage = wordService.getWords(pageSize, page, webUtils.getCurrentUserId());

        return new PageDto<>(wordPage.getContent().stream().map(WordDto::new).toList(), wordPage.getTotalPages());
    }

    @PostMapping
    public WordDto add(@RequestBody WordDto wordDto) {
        User user = webUtils.getCurrentUser();

        Word word = wordService.addWord(new Word(wordDto.japanese(), wordDto.english(), wordDto.kana(), user), user.getUserSettings().getUseAlwaysVoiceVox());

        return new WordDto(word);
    }

    @PutMapping
    public WordDto update(@RequestBody WordDto wordDto) {
        User user = webUtils.getCurrentUser();

        Word word = wordService.updateWord(new Word(wordDto.id(), wordDto.japanese(), wordDto.english(), wordDto.kana(), user), user.getUserSettings().getUseAlwaysVoiceVox());

        return new WordDto(word);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return wordService.deleteWord(id, webUtils.getCurrentUserId());
    }

}

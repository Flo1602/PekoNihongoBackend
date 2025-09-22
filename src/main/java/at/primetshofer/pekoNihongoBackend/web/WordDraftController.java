package at.primetshofer.pekoNihongoBackend.web;

import at.primetshofer.pekoNihongoBackend.dto.PageDto;
import at.primetshofer.pekoNihongoBackend.dto.WordDto;
import at.primetshofer.pekoNihongoBackend.entity.User;
import at.primetshofer.pekoNihongoBackend.entity.WordDraft;
import at.primetshofer.pekoNihongoBackend.security.authentication.AuthConstants;
import at.primetshofer.pekoNihongoBackend.service.WordDraftService;
import at.primetshofer.pekoNihongoBackend.utils.WebUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/words/drafts")
@SecurityRequirement(name = AuthConstants.SECURITY_SCHEME_NAME)
public class WordDraftController {

    private final WordDraftService wordDraftService;
    private final WebUtils webUtils;

    public WordDraftController(WordDraftService wordDraftService,WebUtils webUtils) {
        this.wordDraftService = wordDraftService;
        this.webUtils = webUtils;
    }

    @GetMapping
    public PageDto<WordDto> get(@RequestParam int pageSize, @RequestParam int page) {
        Page<WordDraft> wordPage;

        wordPage = wordDraftService.getWords(pageSize, page, webUtils.getCurrentUserId());

        return new PageDto<>(wordPage.getContent().stream().map(WordDto::new).toList(), wordPage.getTotalPages());
    }

    @PostMapping
    public WordDto add(@RequestBody WordDto wordDto) {
        User user = webUtils.getCurrentUser();

        WordDraft word = wordDraftService.addWord(new WordDraft(wordDto.japanese(), wordDto.english(), wordDto.kana(), user));

        return new WordDto(word);
    }

    @PutMapping
    public WordDto update(@RequestBody WordDto wordDto) {
        User user = webUtils.getCurrentUser();

        WordDraft word = wordDraftService.updateWord(new WordDraft(wordDto.id(), wordDto.japanese(), wordDto.english(), wordDto.kana(), user));

        return new WordDto(word);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return wordDraftService.deleteWord(id, webUtils.getCurrentUserId());
    }

    @PostMapping("/{id}/activate")
    public boolean setActiveVocab(@PathVariable Long id){
        return wordDraftService.setDraftAsActiveVocab(id, webUtils.getCurrentUser());
    }

    @GetMapping("/search")
    public List<WordDto> searchJisho(@RequestParam String search) {
        return wordDraftService.searchWordOnJisho(search, 3);
    }
}

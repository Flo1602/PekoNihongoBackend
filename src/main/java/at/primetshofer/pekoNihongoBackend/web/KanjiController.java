package at.primetshofer.pekoNihongoBackend.web;

import at.primetshofer.pekoNihongoBackend.dto.KanjiWordsDto;
import at.primetshofer.pekoNihongoBackend.dto.PageDto;
import at.primetshofer.pekoNihongoBackend.entity.Kanji;
import at.primetshofer.pekoNihongoBackend.security.authentication.AuthConstants;
import at.primetshofer.pekoNihongoBackend.service.KanjiService;
import at.primetshofer.pekoNihongoBackend.utils.WebUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kanji")
@SecurityRequirement(name = AuthConstants.SECURITY_SCHEME_NAME)
public class KanjiController {

    private final KanjiService kanjiService;
    private final WebUtils webUtils;

    public KanjiController(KanjiService kanjiService, WebUtils webUtils) {
        this.kanjiService = kanjiService;
        this.webUtils = webUtils;
    }

    @GetMapping
    public PageDto<KanjiWordsDto> get(@RequestParam int pageSize, @RequestParam int page) {
        Page<Kanji> kanjiPage = kanjiService.getKanji(pageSize, page, webUtils.getCurrentUserId());

        return new PageDto<>(kanjiPage.getContent().stream().map(KanjiWordsDto::new).toList(), kanjiPage.getTotalPages());
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return kanjiService.deleteKanji(id, webUtils.getCurrentUserId());
    }

}

package at.primetshofer.pekoNihongoBackend.web;

import at.primetshofer.pekoNihongoBackend.dto.QuestDto;
import at.primetshofer.pekoNihongoBackend.entity.Quest;
import at.primetshofer.pekoNihongoBackend.security.authentication.AuthConstants;
import at.primetshofer.pekoNihongoBackend.service.QuestService;
import at.primetshofer.pekoNihongoBackend.utils.WebUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quests")
@SecurityRequirement(name = AuthConstants.SECURITY_SCHEME_NAME)
public class QuestController {

    private final WebUtils webUtils;
    private final QuestService questService;

    public QuestController(WebUtils webUtils, QuestService questService) {
        this.webUtils = webUtils;
        this.questService = questService;
    }

    @GetMapping("/daily")
    public List<QuestDto> getDailyQuests(){
        List<Quest> dailyQuests = questService.getDailyQuests(webUtils.getCurrentUserId());

        return dailyQuests.stream().map(QuestDto::new).toList();
    }

    @PostMapping
    public QuestDto add(@RequestBody QuestDto questDto) {
        Quest quest = questService.addQuest(new Quest(
                questDto.type(),
                questDto.category(),
                questDto.text(),
                questDto.goal()), webUtils.getCurrentUser());

        return new QuestDto(quest);
    }

    @PutMapping
    public QuestDto update(@RequestBody QuestDto questDto) {
        Quest quest = questService.updateQuest(new Quest(
                questDto.id(),
                questDto.type(),
                questDto.category(),
                questDto.text(),
                questDto.goal(),
                questDto.progress()), webUtils.getCurrentUserId());

        return new QuestDto(quest);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return questService.deleteQuest(id, webUtils.getCurrentUserId());
    }

}

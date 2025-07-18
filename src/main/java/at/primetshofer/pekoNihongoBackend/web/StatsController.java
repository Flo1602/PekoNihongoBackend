package at.primetshofer.pekoNihongoBackend.web;

import at.primetshofer.pekoNihongoBackend.dto.StatsDto;
import at.primetshofer.pekoNihongoBackend.security.authentication.AuthConstants;
import at.primetshofer.pekoNihongoBackend.service.StatsService;
import at.primetshofer.pekoNihongoBackend.utils.WebUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
@SecurityRequirement(name = AuthConstants.SECURITY_SCHEME_NAME)
public class StatsController {

    private final WebUtils webUtils;
    private final StatsService statsService;

    public StatsController(WebUtils webUtils, StatsService statsService) {
        this.webUtils = webUtils;
        this.statsService = statsService;
    }

    @PostMapping
    public void add(@RequestBody StatsDto statsDto) {
        statsService.addStat(statsDto.duration(), webUtils.getCurrentUser());
    }

}

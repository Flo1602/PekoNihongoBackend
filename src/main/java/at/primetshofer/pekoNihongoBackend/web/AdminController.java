package at.primetshofer.pekoNihongoBackend.web;

import at.primetshofer.pekoNihongoBackend.dto.RegisterDto;
import at.primetshofer.pekoNihongoBackend.dto.UserDto;
import at.primetshofer.pekoNihongoBackend.service.AuthenticationService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@ConditionalOnProperty(name = "pekoNihongoBackend.admin.enabled", havingValue = "true", matchIfMissing = false)
public class AdminController {

    private final AuthenticationService authenticationService;

    public AdminController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping
    public UserDto all(@RequestBody RegisterDto registerDto) {
        return new UserDto(authenticationService.registerAdmin(registerDto));
    }

}

package at.primetshofer.pekoNihongoBackend.web;

import at.primetshofer.pekoNihongoBackend.security.dto.LoginDto;
import at.primetshofer.pekoNihongoBackend.security.dto.TokenDto;
import at.primetshofer.pekoNihongoBackend.service.AuthenticationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/login")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping
    public TokenDto login(@RequestBody LoginDto loginDto) {
        TokenDto login = authenticationService.login(loginDto);
        return login;
    }

}

package at.primetshofer.pekoNihongoBackend.web;

import at.primetshofer.pekoNihongoBackend.security.authentication.AuthConstants;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/helloWorld")
@SecurityRequirement(name = AuthConstants.SECURITY_SCHEME_NAME)
public class HelloWorldController {

    @GetMapping
    public String helloWorld() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getPrincipal().toString();
    }

}

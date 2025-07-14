package at.primetshofer.pekoNihongoBackend.web;

import at.primetshofer.pekoNihongoBackend.dto.RegisterDto;
import at.primetshofer.pekoNihongoBackend.dto.UserDto;
import at.primetshofer.pekoNihongoBackend.security.authentication.AuthConstants;
import at.primetshofer.pekoNihongoBackend.security.exception.ApplicationAuthenticationException;
import at.primetshofer.pekoNihongoBackend.security.user.AuthUser;
import at.primetshofer.pekoNihongoBackend.security.user.Role;
import at.primetshofer.pekoNihongoBackend.service.AuthenticationService;
import at.primetshofer.pekoNihongoBackend.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@SecurityRequirement(name = AuthConstants.SECURITY_SCHEME_NAME)
public class UserController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    public UserController(UserService userService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @GetMapping("/currentUser")
    public UserDto get() {
        AuthUser authUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return new UserDto(userService.getUserById(authUser.userId()));
    }

    @GetMapping
    public List<UserDto> all() {
        checkAdmin();
        List<UserDto> list = userService.getAllUsers().stream().map(UserDto::new).toList();

        System.out.println(list.size());

        return list;
    }

    @PostMapping
    public UserDto add(RegisterDto registerDto) {
        checkAdmin();
        return new UserDto(authenticationService.registerUser(registerDto));
    }

    @PutMapping
    public UserDto update(@RequestBody RegisterDto registerDto) {
        checkAdmin();
        return new UserDto(authenticationService.updateUser(registerDto));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        checkAdmin();
        userService.deleteUser(id);
    }

    private void checkAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean admin = authentication.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals(Role.ADMIN.name()));

        if(!admin) {
            throw new ApplicationAuthenticationException("Only Administrators can access this resource!");
        }
    }

}

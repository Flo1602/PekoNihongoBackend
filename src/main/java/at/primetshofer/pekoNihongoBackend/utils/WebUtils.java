package at.primetshofer.pekoNihongoBackend.utils;

import at.primetshofer.pekoNihongoBackend.entity.User;
import at.primetshofer.pekoNihongoBackend.security.user.AuthUser;
import at.primetshofer.pekoNihongoBackend.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class WebUtils {

    private final UserService userService;

    public WebUtils(UserService userService) {
        this.userService = userService;
    }

    public User getCurrentUser() {
        return userService.getUserById(getCurrentUserId());
    }

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthUser authUser = (AuthUser) authentication.getPrincipal();

        return authUser.userId();
    }
}

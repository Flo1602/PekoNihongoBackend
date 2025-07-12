package at.primetshofer.pekoNihongoBackend.security.user;

import java.util.List;

public record AuthUser(Long userId, List<Role> roles) {
}

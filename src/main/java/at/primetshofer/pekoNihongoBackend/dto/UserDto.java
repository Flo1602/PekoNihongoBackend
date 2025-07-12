package at.primetshofer.pekoNihongoBackend.dto;

import at.primetshofer.pekoNihongoBackend.entity.User;
import at.primetshofer.pekoNihongoBackend.security.user.Role;

public record UserDto(Long id, String username, Role role) {
    public UserDto(User user){
        this(user.getId(), user.getUsername(), user.getUserRole());
    }
}

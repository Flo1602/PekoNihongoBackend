package at.primetshofer.pekoNihongoBackend.service;

import at.primetshofer.pekoNihongoBackend.dto.RegisterDto;
import at.primetshofer.pekoNihongoBackend.dto.UserDto;
import at.primetshofer.pekoNihongoBackend.entity.User;
import at.primetshofer.pekoNihongoBackend.security.dto.LoginDto;
import at.primetshofer.pekoNihongoBackend.security.dto.TokenDto;
import at.primetshofer.pekoNihongoBackend.security.exception.ApplicationAuthenticationException;
import at.primetshofer.pekoNihongoBackend.security.service.JwtService;
import at.primetshofer.pekoNihongoBackend.security.user.AuthUser;
import at.primetshofer.pekoNihongoBackend.security.user.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthenticationService {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    public AuthenticationService(UserService userService, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public TokenDto login(LoginDto loginDto) {

        User user = userService.getUserByUsername(loginDto.username());

        if (!passwordEncoder.matches(loginDto.password(), user.getPassword())) {
            throw new ApplicationAuthenticationException("Password is incorrect");
        }

        AuthUser authUser = new AuthUser(user.getId(), List.of(user.getUserRole()));

        String jwtToken = jwtService.createJwtToken(authUser);

        return new TokenDto(jwtToken);
    }

    public User registerAdmin(RegisterDto registerDto) {
        String hashedPassword = passwordEncoder.encode(registerDto.password());

        return userService.addUser(new User(registerDto.username(), hashedPassword, Role.ADMIN));
    }

    public User registerUser(RegisterDto registerDto) {
        String hashedPassword = passwordEncoder.encode(registerDto.password());

        return userService.addUser(new User(registerDto.username(), hashedPassword, Role.USER));
    }

    public User updateUser(RegisterDto registerDto) {
        String hashedPassword = passwordEncoder.encode(registerDto.password());

        return userService.updateUser(new User(registerDto.username(), hashedPassword, Role.USER));
    }

}

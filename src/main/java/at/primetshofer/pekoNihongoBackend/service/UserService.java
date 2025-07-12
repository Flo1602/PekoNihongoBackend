package at.primetshofer.pekoNihongoBackend.service;

import at.primetshofer.pekoNihongoBackend.entity.User;
import at.primetshofer.pekoNihongoBackend.repository.UserRepository;
import at.primetshofer.pekoNihongoBackend.security.user.Role;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserByUsername(String username) {
        List<User> usersByUsername = userRepository.getUsersByUsername(username);
        if (usersByUsername.isEmpty()) {
            return null;
        } else {
            return usersByUsername.getFirst();
        }
    }

    public User addUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public User updateUser(User user) {
        if (user.getId() == null) {
            throw new IllegalArgumentException("User id is null");
        }
        Optional<User> oldUser = userRepository.findById(user.getId());
        if (oldUser.isEmpty()) {
            throw new IllegalArgumentException("User with id " + user.getId() + " does not exist");
        }
        User updatedUser = oldUser.get();

        updatedUser.setUsername(user.getUsername());
        updatedUser.setPassword(user.getPassword());
        updatedUser.setUserRole(Role.USER);

        return userRepository.save(updatedUser);
    }
}

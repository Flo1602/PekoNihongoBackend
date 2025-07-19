package at.primetshofer.pekoNihongoBackend.service;

import at.primetshofer.pekoNihongoBackend.entity.User;
import at.primetshofer.pekoNihongoBackend.entity.UserSettings;
import at.primetshofer.pekoNihongoBackend.repository.UserRepository;
import at.primetshofer.pekoNihongoBackend.security.user.Role;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

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

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
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

    public void updateUserSettings(Long userId, UserSettings userSettings) {
        if (userId == null) {
            throw new IllegalArgumentException("User id is null");
        }
        Optional<User> oldUser = userRepository.findById(userId);
        if (oldUser.isEmpty()) {
            throw new IllegalArgumentException("User with id " + userId + " does not exist");
        }

        User updatedUser = oldUser.get();

        if (updatedUser.getUserSettings() == null) {
            updatedUser.setUserSettings(new UserSettings());
        }

        updatedUser.getUserSettings().setVoiceId(userSettings.getVoiceId());
        updatedUser.getUserSettings().setMaxDailyWords(userSettings.getMaxDailyWords());
        updatedUser.getUserSettings().setMaxDailyKanji(userSettings.getMaxDailyKanji());
        updatedUser.getUserSettings().setUseAlwaysVoiceVox(userSettings.getUseAlwaysVoiceVox());

        userRepository.save(updatedUser);
    }
}

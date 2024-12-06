package org.citywatcher.service;

import org.citywatcher.model.User;
import org.citywatcher.model.UserRole;
import org.citywatcher.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, FileStorageService fileStorageService) {
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public User registerUser(User user, MultipartFile imageFile) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("Email already exists");
        }
        if (user.getRole() == null) {
            user.setRole(UserRole.CITIZEN);
        }
        if (imageFile != null && !imageFile.isEmpty()) {
            String imagePath = fileStorageService.saveProfileImage(user.getUsername(), imageFile);
            user.setProfileImagePath(imagePath);
        }
        return userRepository.save(user);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(Long id, User userDetails, MultipartFile imageFile) {
        User existingUser = getUserById(id);
        if (existingUser == null) {
            return null;
        }

        existingUser.setUsername(userDetails.getUsername());
        existingUser.setEmail(userDetails.getEmail());
        existingUser.setRole(userDetails.getRole());

        // Only update password if it's provided
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            existingUser.setPassword(userDetails.getPassword());
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            if (existingUser.getProfileImagePath() != null) {
                fileStorageService.deleteFile(existingUser.getProfileImagePath());
            }
            String imagePath = fileStorageService.saveProfileImage(existingUser.getUsername(), imageFile);
            existingUser.setProfileImagePath(imagePath);
        }

        return userRepository.save(existingUser);
    }

    @Override
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
}

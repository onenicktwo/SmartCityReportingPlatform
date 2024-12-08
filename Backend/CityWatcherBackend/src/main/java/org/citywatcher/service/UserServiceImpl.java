package org.citywatcher.service;

import org.citywatcher.model.Issue;
import org.citywatcher.model.User;
import org.citywatcher.model.UserRole;
import org.citywatcher.repository.IssueRepository;
import org.citywatcher.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final IssueRepository issueRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, FileStorageService fileStorageService) {
    public UserServiceImpl(UserRepository userRepository, IssueRepository issueRepository) {
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
        this.issueRepository = issueRepository;
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

    public void followIssue(Long userId, Long issueId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new IllegalArgumentException("Issue not found"));

        if (user.getFollowedIssues().contains(issue)) {
            throw new IllegalArgumentException("Already following this issue");
        }

        user.followIssue(issue);
        userRepository.save(user);
    }

    public void unfollowIssue(Long userId, Long issueId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new IllegalArgumentException("Issue not found"));

        if (!user.getFollowedIssues().contains(issue)) {
            throw new IllegalArgumentException("Not following this issue");
        }

        user.unfollowIssue(issue);
        userRepository.save(user);
    }

    public List<Issue> getFollowedIssues(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return new ArrayList<>(user.getFollowedIssues());
    }
}

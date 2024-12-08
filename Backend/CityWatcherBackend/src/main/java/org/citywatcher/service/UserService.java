package org.citywatcher.service;

import org.citywatcher.model.Issue;
import org.citywatcher.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    User registerUser(User user, MultipartFile imageFile);
    User getUserById(Long id);
    List<User> getAllUsers();
    User updateUser(Long id, User user, MultipartFile imageFile);
    boolean deleteUser(Long id);
    void followIssue(Long userId, Long issueId);
    void unfollowIssue(Long userId, Long issueId);
    List<Issue> getFollowedIssues(Long userId);
}
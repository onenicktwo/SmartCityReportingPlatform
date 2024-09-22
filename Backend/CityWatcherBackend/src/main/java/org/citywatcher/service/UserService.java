package org.citywatcher.service;

import org.citywatcher.model.User;

import java.util.List;

public interface UserService {
    User registerUser(User user);
    User getUserById(Long id);
    List<User> getAllUsers();
    User updateUser(Long id, User user);
    boolean deleteUser(Long id);
}
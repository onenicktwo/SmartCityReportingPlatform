package org.citywatcher.service;

import org.citywatcher.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path rootLocation = Paths.get("uploads");

    public FileStorageServiceImpl() {
        try {
            Files.createDirectories(rootLocation.resolve("issues"));
            Files.createDirectories(rootLocation.resolve("profile"));
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage", e);
        }
    }

    @Override
    public String saveIssueImage(Long userId, byte[] imageBytes, String originalFilename) {
        try {
            String fileName = "issue_" + userId + "_" + System.currentTimeMillis() + getFileExtension(originalFilename);
            Path filePath = rootLocation.resolve("issues").resolve(fileName);

            Files.write(filePath, imageBytes); // Writing byte array to file
            return "issues/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    // Updated method to save profile image from byte array
    @Override
    public String saveProfileImage(String username, byte[] imageBytes, String originalFilename) {
        try {
            String fileName = "profile_" + username + "_" + System.currentTimeMillis() + getFileExtension(originalFilename);
            Path filePath = rootLocation.resolve("profile").resolve(fileName);

            Files.write(filePath, imageBytes); // Writing byte array to file
            return "profile/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    // Method to load a file as a resource
    @Override
    public Resource loadFileAsResource(String filePath) {
        try {
            Path file = rootLocation.resolve(filePath);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("File not found: " + filePath);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error loading file: " + filePath, e);
        }
    }

    // Method to delete a file
    @Override
    public void deleteFile(String filePath) {
        try {
            Path file = rootLocation.resolve(filePath);
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + filePath, e);
        }
    }

    // Helper method to extract file extension
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.'));
    }
}
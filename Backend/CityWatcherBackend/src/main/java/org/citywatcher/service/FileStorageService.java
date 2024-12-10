package org.citywatcher.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String saveIssueImage(Long userId, byte[] imageBytes, String originalFilename);

    String saveProfileImage(String username, byte[] imageBytes, String originalFilename);

    Resource loadFileAsResource(String filePath);

    void deleteFile(String filePath);
}

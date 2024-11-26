package org.citywatcher.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String saveIssueImage(Long userId, MultipartFile file);

    String saveProfileImage(String username, MultipartFile file);

    Resource loadFileAsResource(String filePath);

    void deleteFile(String filePath);
}

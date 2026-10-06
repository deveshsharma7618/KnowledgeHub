package com.deveshsharma.knowledgehub.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    void uploadFile(MultipartFile file) throws Exception;

    default void uploadFile(String storageKey, MultipartFile file) throws Exception {
        uploadFile(file);
    }

    default void deleteFile(String storageKey) throws Exception {
        // Implementations that support cleanup override this method.
    }
}

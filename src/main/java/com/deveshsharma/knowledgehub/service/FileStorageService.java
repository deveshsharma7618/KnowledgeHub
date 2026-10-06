package com.deveshsharma.knowledgehub.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    void uploadFile(MultipartFile file) throws Exception;
}

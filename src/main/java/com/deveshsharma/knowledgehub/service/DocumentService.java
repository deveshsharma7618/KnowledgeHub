package com.deveshsharma.knowledgehub.service;

import org.springframework.web.multipart.MultipartFile;

public interface DocumentService {

    void addDocument(String workspaceId, MultipartFile documentFile) throws Exception;
}

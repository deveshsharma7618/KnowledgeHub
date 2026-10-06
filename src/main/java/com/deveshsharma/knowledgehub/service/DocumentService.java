package com.deveshsharma.knowledgehub.service;

import org.springframework.web.multipart.MultipartFile;
import com.deveshsharma.knowledgehub.dto.response.DocumentResponse;
import java.util.List;
import java.util.UUID;

public interface DocumentService {
    DocumentResponse addDocument(String workspaceId, MultipartFile documentFile) throws Exception;
    List<DocumentResponse> findByWorkspace(UUID workspaceId);
    DocumentResponse find(UUID workspaceId, UUID documentId);
    void delete(UUID workspaceId, UUID documentId) throws Exception;
}

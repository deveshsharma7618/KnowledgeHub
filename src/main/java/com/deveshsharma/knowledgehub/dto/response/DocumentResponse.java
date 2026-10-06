package com.deveshsharma.knowledgehub.dto.response;

import com.deveshsharma.knowledgehub.entity.Document;
import java.time.Instant;
import java.util.UUID;

public record DocumentResponse(UUID id, UUID workspaceId, UUID uploadedBy, String filename, String contentType,
                               Long fileSize, String status, Instant createdAt, Instant updatedAt) {
    public static DocumentResponse from(Document document) {
        return new DocumentResponse(document.getId(), document.getWorkspaceId(), document.getUploadedBy(),
                document.getFilename(), document.getContentType(), document.getFileSize(),
                document.getStatus().name(), document.getCreatedAt(), document.getUpdatedAt());
    }
}

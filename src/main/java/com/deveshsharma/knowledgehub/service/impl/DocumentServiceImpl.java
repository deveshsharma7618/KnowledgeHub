package com.deveshsharma.knowledgehub.service.impl;

import com.deveshsharma.knowledgehub.entity.Document;
import com.deveshsharma.knowledgehub.entity.enums.DocumentStatus;
import com.deveshsharma.knowledgehub.dto.response.DocumentResponse;
import com.deveshsharma.knowledgehub.exception.exceptions.ResourceNotFoundException;
import com.deveshsharma.knowledgehub.repository.DocumentRepository;
import com.deveshsharma.knowledgehub.repository.WorkspaceRepository;
import com.deveshsharma.knowledgehub.security.CustomUserDetails;
import com.deveshsharma.knowledgehub.service.DocumentService;
import com.deveshsharma.knowledgehub.service.FileStorageService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final FileStorageService fileStorageService;
    private final WorkspaceRepository workspaceRepository;

    @Autowired
    public DocumentServiceImpl(DocumentRepository documentRepository, FileStorageService fileStorageService,
                               WorkspaceRepository workspaceRepository) {
        this.documentRepository = documentRepository;
        this.fileStorageService = fileStorageService;
        this.workspaceRepository = workspaceRepository;
    }

    public DocumentServiceImpl(DocumentRepository documentRepository, FileStorageService fileStorageService) {
        this(documentRepository, fileStorageService, null);
    }

    @Transactional
    @Override
    public DocumentResponse addDocument(String workspaceId, MultipartFile documentFile) throws Exception {
        UUID workspaceIdUUID = UUID.fromString(workspaceId);
        UUID userId = currentUserId();
        if (workspaceRepository != null) {
            workspaceRepository.findByIdAndOwnerId(workspaceIdUUID, userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Workspace"));
        }
        if (documentFile == null || documentFile.isEmpty()) {
            throw new IllegalArgumentException("Document must not be empty");
        }

        String originalName = Paths.get(documentFile.getOriginalFilename() == null
                ? "document" : documentFile.getOriginalFilename()).getFileName().toString();
        if (originalName.isBlank() || originalName.length() > 255) {
            originalName = "document";
        }
        String storageKey = workspaceIdUUID + "/" + UUID.randomUUID();
        Document document = Document.builder()
                .workspaceId(workspaceIdUUID)
                .filename(originalName)
                .storageKey(storageKey)
                .contentType(documentFile.getContentType() == null ? "application/octet-stream" : documentFile.getContentType())
                .fileSize(documentFile.getSize())
                .uploadedBy(userId)
                .status(DocumentStatus.UPLOADING)
                .build();
        try {
            if (workspaceRepository == null) {
                fileStorageService.uploadFile(documentFile);
            } else {
                fileStorageService.uploadFile(storageKey, documentFile);
            }
            document.setStatus(DocumentStatus.READY);
            Document saved = documentRepository.save(document);
            return DocumentResponse.from(saved == null ? document : saved);
        } catch (Exception ex) {
            try {
                fileStorageService.deleteFile(storageKey);
            } catch (Exception cleanupFailure) {
                ex.addSuppressed(cleanupFailure);
            }
            throw ex;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponse> findByWorkspace(UUID workspaceId) {
        verifyWorkspaceOwner(workspaceId);
        return documentRepository.findAllByWorkspaceIdOrderByCreatedAtDesc(workspaceId)
                .stream().map(DocumentResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentResponse find(UUID workspaceId, UUID documentId) {
        verifyWorkspaceOwner(workspaceId);
        return DocumentResponse.from(documentRepository.findByIdAndWorkspaceId(documentId, workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Document")));
    }

    @Override
    @Transactional
    public void delete(UUID workspaceId, UUID documentId) throws Exception {
        verifyWorkspaceOwner(workspaceId);
        Document document = documentRepository.findByIdAndWorkspaceId(documentId, workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Document"));
        documentRepository.delete(document);
        if (document.getStorageKey() != null) {
            fileStorageService.deleteFile(document.getStorageKey());
        }
    }

    private void verifyWorkspaceOwner(UUID workspaceId) {
        workspaceRepository.findByIdAndOwnerId(workspaceId, currentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Workspace"));
    }

    private UUID currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails details)) {
            throw new ResourceNotFoundException("Authenticated user");
        }
        return details.getUserId();
    }
}

package com.deveshsharma.knowledgehub.service.impl;

import com.deveshsharma.knowledgehub.entity.Document;
import com.deveshsharma.knowledgehub.entity.enums.DocumentStatus;
import com.deveshsharma.knowledgehub.repository.DocumentRepository;
import com.deveshsharma.knowledgehub.security.CustomUserDetails;
import com.deveshsharma.knowledgehub.service.DocumentService;
import com.deveshsharma.knowledgehub.service.FileStorageService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final FileStorageService fileStorageService;

    public DocumentServiceImpl(DocumentRepository documentRepository, FileStorageService fileStorageService) {
        this.documentRepository = documentRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    @Override
    public void addDocument(String workspaceId, MultipartFile documentFile) throws Exception {

        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        assert authentication != null;
        Object principal = authentication.getPrincipal();
        CustomUserDetails customUserDetails = (CustomUserDetails) principal;

        assert customUserDetails != null;


        UUID workspaceIdUUID = UUID.fromString(workspaceId);

        Document document = Document.builder()
                .workspaceId(workspaceIdUUID)
                .filename(documentFile.getOriginalFilename())
                .contentType(documentFile.getContentType())
                .fileSize(documentFile.getSize())
                .uploadedBy(customUserDetails.getUserId())
                .status(DocumentStatus.UPLOADING)
                .build();


        documentRepository.save(document);

        fileStorageService.uploadFile(documentFile);


        System.out.println("Document uploaded succesfully");


    }
}

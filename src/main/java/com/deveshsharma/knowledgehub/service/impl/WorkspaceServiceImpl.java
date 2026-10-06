package com.deveshsharma.knowledgehub.service.impl;

import com.deveshsharma.knowledgehub.dto.request.CreateWorkspaceRequest;
import com.deveshsharma.knowledgehub.dto.request.UpdateWorkspaceRequest;
import com.deveshsharma.knowledgehub.dto.response.WorkspaceDetailsResponse;
import com.deveshsharma.knowledgehub.entity.Workspace;
import com.deveshsharma.knowledgehub.entity.Document;
import com.deveshsharma.knowledgehub.exception.exceptions.ResourceNotFoundException;
import com.deveshsharma.knowledgehub.repository.DocumentRepository;
import com.deveshsharma.knowledgehub.repository.WorkspaceRepository;
import com.deveshsharma.knowledgehub.security.CustomUserDetails;
import com.deveshsharma.knowledgehub.service.WorkspaceService;
import com.deveshsharma.knowledgehub.service.FileStorageService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.UUID;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final DocumentRepository documentRepository;
    private final FileStorageService fileStorageService;

    @Autowired
    public WorkspaceServiceImpl(WorkspaceRepository workspaceRepository, DocumentRepository documentRepository,
                                FileStorageService fileStorageService) {
        this.workspaceRepository = workspaceRepository;
        this.documentRepository = documentRepository;
        this.fileStorageService = fileStorageService;
    }

    public WorkspaceServiceImpl(WorkspaceRepository workspaceRepository) {
        this(workspaceRepository, null, null);
    }

    @Override
    @Transactional
    public WorkspaceDetailsResponse create(CreateWorkspaceRequest createWorkspaceRequest) {
        UUID userId = currentUserId();
        Workspace workspace = Workspace.builder()
                .ownerId(userId)
                .name(createWorkspaceRequest.getWorkspaceName().trim())
                .build();
        Workspace saved = workspaceRepository.save(workspace);
        return WorkspaceDetailsResponse.from(saved == null ? workspace : saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkspaceDetailsResponse> findMine() {
        return workspaceRepository.findAllByOwnerIdOrderByCreatedAtDesc(currentUserId())
                .stream().map(WorkspaceDetailsResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public WorkspaceDetailsResponse find(UUID workspaceId) {
        return WorkspaceDetailsResponse.from(ownedWorkspace(workspaceId));
    }

    @Override
    @Transactional
    public WorkspaceDetailsResponse update(UUID workspaceId, UpdateWorkspaceRequest request) {
        Workspace workspace = ownedWorkspace(workspaceId);
        workspace.setName(request.getWorkspaceName().trim());
        return WorkspaceDetailsResponse.from(workspace);
    }

    @Override
    @Transactional
    public void delete(UUID workspaceId) {
        Workspace workspace = ownedWorkspace(workspaceId);
        if (documentRepository != null) {
            for (Document document : documentRepository.findAllByWorkspaceIdOrderByCreatedAtDesc(workspaceId)) {
                if (document.getStorageKey() != null && fileStorageService != null) {
                    try {
                        fileStorageService.deleteFile(document.getStorageKey());
                    } catch (Exception ex) {
                        throw new IllegalStateException("Could not delete workspace documents", ex);
                    }
                }
            }
            documentRepository.deleteAllByWorkspaceId(workspaceId);
        }
        workspaceRepository.delete(workspace);
    }

    private Workspace ownedWorkspace(UUID workspaceId) {
        return workspaceRepository.findByIdAndOwnerId(workspaceId, currentUserId())
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

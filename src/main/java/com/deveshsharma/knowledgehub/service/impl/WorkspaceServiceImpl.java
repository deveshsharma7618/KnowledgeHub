package com.deveshsharma.knowledgehub.service.impl;

import com.deveshsharma.knowledgehub.dto.request.CreateWorkspaceRequest;
import com.deveshsharma.knowledgehub.dto.request.UpdateWorkspaceRequest;
import com.deveshsharma.knowledgehub.dto.response.WorkspaceDetailsResponse;
import com.deveshsharma.knowledgehub.entity.Workspace;
import com.deveshsharma.knowledgehub.entity.Document;
import com.deveshsharma.knowledgehub.entity.WorkspaceMember;
import com.deveshsharma.knowledgehub.entity.enums.WorkspaceRole;
import com.deveshsharma.knowledgehub.exception.exceptions.ForbiddenOperationException;
import com.deveshsharma.knowledgehub.exception.exceptions.ResourceNotFoundException;
import com.deveshsharma.knowledgehub.repository.DocumentRepository;
import com.deveshsharma.knowledgehub.repository.UserRepository;
import com.deveshsharma.knowledgehub.repository.WorkspaceMemberRepository;
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
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final UserRepository userRepository;

    @Autowired
    public WorkspaceServiceImpl(WorkspaceRepository workspaceRepository, DocumentRepository documentRepository,
                                FileStorageService fileStorageService,
                                WorkspaceMemberRepository workspaceMemberRepository,
                                UserRepository userRepository) {
        this.workspaceRepository = workspaceRepository;
        this.documentRepository = documentRepository;
        this.fileStorageService = fileStorageService;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.userRepository = userRepository;
    }

    public WorkspaceServiceImpl(WorkspaceRepository workspaceRepository) {
        this(workspaceRepository, null, null, null, null);
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
        if (workspaceMemberRepository != null) {
            workspaceMemberRepository.save(WorkspaceMember.builder()
                    .workspaceId(saved == null ? workspace.getId() : saved.getId())
                    .userId(userId)
                    .role(WorkspaceRole.OWNER)
                    .build());
        }
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
        if (workspaceMemberRepository != null) {
            workspaceMemberRepository.deleteByWorkspaceId(workspaceId);
        }
        workspaceRepository.delete(workspace);
    }

    @Override
    @Transactional
    public void addMember(UUID workspaceId, UUID userId) {
        ownedWorkspace(workspaceId);
        if (userRepository == null || userRepository.findById(userId).isEmpty()) {
            throw new ResourceNotFoundException("User");
        }
        if (workspaceMemberRepository.existsByWorkspaceIdAndUserId(workspaceId, userId)) {
            throw new IllegalArgumentException("User is already a member of this workspace");
        }
        workspaceMemberRepository.save(WorkspaceMember.builder()
                .workspaceId(workspaceId)
                .userId(userId)
                .role(WorkspaceRole.MEMBER)
                .build());
    }

    @Override
    @Transactional
    public void leave(UUID workspaceId) {
        WorkspaceMember member = member(workspaceId, currentUserId());
        if (member.getRole() == WorkspaceRole.OWNER) {
            throw new ForbiddenOperationException();
        }
        workspaceMemberRepository.delete(member);
    }

    @Override
    @Transactional
    public void removeMember(UUID workspaceId, UUID userId) {
        ownedWorkspace(workspaceId);
        WorkspaceMember member = member(workspaceId, userId);
        if (member.getRole() == WorkspaceRole.OWNER) {
            throw new ForbiddenOperationException();
        }
        workspaceMemberRepository.delete(member);
    }

    private WorkspaceMember member(UUID workspaceId, UUID userId) {
        if (workspaceMemberRepository == null) {
            throw new ResourceNotFoundException("Workspace member");
        }
        return workspaceMemberRepository.findByWorkspaceIdAndUserId(workspaceId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace member"));
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

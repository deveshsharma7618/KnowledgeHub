package com.deveshsharma.knowledgehub.service.impl;

import com.deveshsharma.knowledgehub.dto.request.CreateWorkspaceRequest;
import com.deveshsharma.knowledgehub.entity.Workspace;
import com.deveshsharma.knowledgehub.repository.WorkspaceRepository;
import com.deveshsharma.knowledgehub.security.CustomUserDetails;
import com.deveshsharma.knowledgehub.service.WorkspaceService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {

    private final WorkspaceRepository workspaceRepository;

    public WorkspaceServiceImpl(WorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    @Override
    public void create(CreateWorkspaceRequest createWorkspaceRequest) {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        assert authentication != null;
        Object principal = authentication.getPrincipal();
        CustomUserDetails customUserDetails = (CustomUserDetails) principal;

        assert customUserDetails != null;

        Workspace workspace = Workspace.builder()
                .ownerId(customUserDetails.getUserId())
                .name(createWorkspaceRequest.getWorkspaceName())
                .build();
        workspaceRepository.save(workspace);

        System.out.println(workspace.toString());
    }


}

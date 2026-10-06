package com.deveshsharma.knowledgehub.service;

import com.deveshsharma.knowledgehub.dto.request.CreateWorkspaceRequest;
import com.deveshsharma.knowledgehub.dto.request.UpdateWorkspaceRequest;
import com.deveshsharma.knowledgehub.dto.response.WorkspaceDetailsResponse;
import java.util.List;
import java.util.UUID;

public interface WorkspaceService {
    WorkspaceDetailsResponse create(CreateWorkspaceRequest createWorkspaceRequest);
    List<WorkspaceDetailsResponse> findMine();
    WorkspaceDetailsResponse find(UUID workspaceId);
    WorkspaceDetailsResponse update(UUID workspaceId, UpdateWorkspaceRequest request);
    void delete(UUID workspaceId);
}

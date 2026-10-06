package com.deveshsharma.knowledgehub.service;

import com.deveshsharma.knowledgehub.dto.request.CreateWorkspaceRequest;

public interface WorkspaceService {

    void create(CreateWorkspaceRequest createWorkspaceRequest);
}

package com.deveshsharma.knowledgehub.dto.response;

import com.deveshsharma.knowledgehub.entity.Workspace;
import java.time.Instant;
import java.util.UUID;

public record WorkspaceDetailsResponse(UUID id, String name, UUID ownerId, Instant createdAt, Instant updatedAt) {
    public static WorkspaceDetailsResponse from(Workspace workspace) {
        return new WorkspaceDetailsResponse(workspace.getId(), workspace.getName(), workspace.getOwnerId(),
                workspace.getCreatedAt(), workspace.getUpdatedAt());
    }
}

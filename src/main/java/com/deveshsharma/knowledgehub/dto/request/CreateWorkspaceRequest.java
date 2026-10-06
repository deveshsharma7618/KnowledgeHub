package com.deveshsharma.knowledgehub.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CreateWorkspaceRequest {
    @NotEmpty(
            message = "Workspace name can't be empty"
    )
    String workspaceName;
}

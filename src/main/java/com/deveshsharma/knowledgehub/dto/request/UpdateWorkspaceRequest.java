package com.deveshsharma.knowledgehub.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateWorkspaceRequest {
    @NotBlank
    @Size(max = 150)
    private String workspaceName;
}

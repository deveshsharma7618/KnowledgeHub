package com.deveshsharma.knowledgehub.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddWorkspaceMemberRequest(
        @NotNull(message = "User id can't be empty")
        UUID userId
) {
}

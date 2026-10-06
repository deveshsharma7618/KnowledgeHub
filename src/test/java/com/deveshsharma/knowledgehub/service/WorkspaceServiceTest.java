package com.deveshsharma.knowledgehub.service;

import com.deveshsharma.knowledgehub.dto.request.CreateWorkspaceRequest;
import com.deveshsharma.knowledgehub.entity.User;
import com.deveshsharma.knowledgehub.entity.Workspace;
import com.deveshsharma.knowledgehub.security.CustomUserDetails;
import com.deveshsharma.knowledgehub.repository.WorkspaceRepository;
import com.deveshsharma.knowledgehub.service.impl.WorkspaceServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WorkspaceServiceTest {

    private final WorkspaceRepository repository = mock(WorkspaceRepository.class);
    private final WorkspaceService service = new WorkspaceServiceImpl(repository);

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createsWorkspaceForAuthenticatedUser() {
        UUID userId = UUID.randomUUID();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        new CustomUserDetails(User.builder().id(userId).email("owner@example.com").build()),
                        null
                )
        );

        service.create(new CreateWorkspaceRequest("Engineering"));

        var captor = org.mockito.ArgumentCaptor.forClass(Workspace.class);
        verify(repository).save(captor.capture());
        assertEquals(userId, captor.getValue().getOwnerId());
        assertEquals("Engineering", captor.getValue().getName());
    }
}

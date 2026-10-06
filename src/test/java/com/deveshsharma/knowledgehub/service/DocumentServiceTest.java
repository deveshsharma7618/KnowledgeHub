package com.deveshsharma.knowledgehub.service;

import com.deveshsharma.knowledgehub.entity.Document;
import com.deveshsharma.knowledgehub.entity.User;
import com.deveshsharma.knowledgehub.repository.DocumentRepository;
import com.deveshsharma.knowledgehub.security.CustomUserDetails;
import com.deveshsharma.knowledgehub.service.impl.DocumentServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DocumentServiceTest {

    private final DocumentRepository repository = mock(DocumentRepository.class);
    private final FileStorageService storageService = mock(FileStorageService.class);
    private final DocumentService service = new DocumentServiceImpl(repository, storageService);

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void persistsMetadataAndUploadsFile() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID workspaceId = UUID.randomUUID();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        new CustomUserDetails(User.builder().id(userId).email("owner@example.com").build()),
                        null
                )
        );
        MockMultipartFile file = new MockMultipartFile("file", "notes.txt", "text/plain", "content".getBytes());

        service.addDocument(workspaceId.toString(), file);

        var captor = org.mockito.ArgumentCaptor.forClass(Document.class);
        verify(repository).save(captor.capture());
        verify(storageService).uploadFile(file);
        assertEquals(workspaceId, captor.getValue().getWorkspaceId());
        assertEquals(userId, captor.getValue().getUploadedBy());
        assertEquals("notes.txt", captor.getValue().getFilename());
    }
}

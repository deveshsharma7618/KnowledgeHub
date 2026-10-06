package com.deveshsharma.knowledgehub.repository;

import com.deveshsharma.knowledgehub.entity.Document;
import com.deveshsharma.knowledgehub.entity.RefreshToken;
import com.deveshsharma.knowledgehub.entity.User;
import com.deveshsharma.knowledgehub.entity.Workspace;
import com.deveshsharma.knowledgehub.entity.enums.DocumentStatus;
import com.deveshsharma.knowledgehub.entity.enums.UserStatus;
import com.deveshsharma.knowledgehub.KnowledgeHubApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ContextConfiguration(classes = KnowledgeHubApplication.class)
class AllRepositoryTest {

    @Autowired UserRepository userRepository;
    @Autowired RefreshTokenRepository refreshTokenRepository;
    @Autowired WorkspaceRepository workspaceRepository;
    @Autowired DocumentRepository documentRepository;

    @Test
    void persistsWorkspaceDocumentAndRefreshToken() {
        User user = userRepository.save(User.builder()
                .email("repo-all@example.com").passwordHash("hash").name("Repo User")
                .status(UserStatus.ACTIVE).build());
        Workspace workspace = workspaceRepository.save(Workspace.builder()
                .ownerId(user.getId()).name("Workspace").build());
        Document document = documentRepository.save(Document.builder()
                .workspaceId(workspace.getId()).uploadedBy(user.getId()).filename("a.txt")
                .contentType("text/plain").fileSize(1L).status(DocumentStatus.UPLOADING).build());
        RefreshToken refreshToken = refreshTokenRepository.save(RefreshToken.builder()
                .userId(user.getId()).tokenHash(UUID.randomUUID().toString())
                .expiresAt(Instant.now().plusSeconds(60)).build());

        assertNotNull(workspace.getId());
        assertNotNull(document.getId());
        assertNotNull(refreshToken.getId());
        assertEquals(1, refreshTokenRepository.revokeActiveTokens(user.getId(), Instant.now()));
    }
}

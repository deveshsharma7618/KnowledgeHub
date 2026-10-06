package com.deveshsharma.knowledgehub.repository;

import com.deveshsharma.knowledgehub.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, UUID> {
    List<Document> findAllByWorkspaceIdOrderByCreatedAtDesc(UUID workspaceId);
    java.util.Optional<Document> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    void deleteAllByWorkspaceId(UUID workspaceId);
}

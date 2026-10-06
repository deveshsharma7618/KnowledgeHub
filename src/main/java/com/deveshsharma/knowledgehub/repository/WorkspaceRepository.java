package com.deveshsharma.knowledgehub.repository;

import com.deveshsharma.knowledgehub.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;

public interface WorkspaceRepository extends JpaRepository<Workspace, UUID> {
    List<Workspace> findAllByOwnerIdOrderByCreatedAtDesc(UUID ownerId);
    java.util.Optional<Workspace> findByIdAndOwnerId(UUID id, UUID ownerId);
}

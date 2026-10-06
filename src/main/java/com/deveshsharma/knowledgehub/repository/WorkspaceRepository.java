package com.deveshsharma.knowledgehub.repository;

import com.deveshsharma.knowledgehub.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WorkspaceRepository extends JpaRepository<Workspace, UUID> {
}

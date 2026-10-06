package com.deveshsharma.knowledgehub.repository;

import com.deveshsharma.knowledgehub.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {
}

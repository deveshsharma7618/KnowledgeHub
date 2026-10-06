package com.deveshsharma.knowledgehub.entity;

import com.deveshsharma.knowledgehub.entity.enums.DocumentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

/**
 * Persistent metadata for a document uploaded to a workspace.
 */
@Entity
@Table(name = "documents")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID workspaceId;

    @Column(nullable = false)
    private UUID uploadedBy;

    @Column(nullable = false)
    private String filename;

    @Column(unique = true)
    private String storageKey;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private Long fileSize;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentStatus status;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Instant createdAt;

    private Instant updatedAt;
}

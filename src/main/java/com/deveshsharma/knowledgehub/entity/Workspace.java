package com.deveshsharma.knowledgehub.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Persistent workspace metadata and ownership information.
 */
@Entity
@Table(name = "workspaces")
public class Workspace {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private UUID ownerId;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant updatedAt;
}
package com.deveshsharma.knowledgehub.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Persistent hashed refresh-token metadata for a user session.
 */
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false, unique = true)
    private String tokenHash;

    @Column(nullable = false)
    private Instant expiresAt;

    private Instant revokedAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}

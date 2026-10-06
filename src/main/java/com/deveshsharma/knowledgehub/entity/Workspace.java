package com.deveshsharma.knowledgehub.entity;

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
 * Persistent workspace metadata and ownership information.
 */
@Entity
@Table(name = "workspaces")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Workspace {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private UUID ownerId;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Instant createdAt;

    private Instant updatedAt;
}
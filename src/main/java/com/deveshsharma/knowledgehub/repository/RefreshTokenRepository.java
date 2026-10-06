package com.deveshsharma.knowledgehub.repository;

import com.deveshsharma.knowledgehub.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    @Modifying
    @Query("""
            update RefreshToken token
            set token.revokedAt = :revokedAt
            where token.userId = :userId
              and token.revokedAt is null
            """)
    int revokeActiveTokens(@Param("userId") UUID userId, @Param("revokedAt") Instant revokedAt);
}

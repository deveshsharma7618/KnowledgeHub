package com.deveshsharma.knowledgehub.repository;

import com.deveshsharma.knowledgehub.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

import java.time.Instant;
import java.util.UUID;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    @Modifying
    @Query("""
            update RefreshToken token
            set token.revokedAt = :revokedAt
            where token.userId = :userId
              and token.revokedAt is null
            """)
    int revokeActiveTokens(@Param("userId") UUID userId, @Param("revokedAt") Instant revokedAt);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<RefreshToken> findByTokenHashAndRevokedAtIsNull(String tokenHash);
}

package com.posit.posit.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Component
@Getter
public class JwtUtil {
    private final SecretKey key;
    private final long accessTokenExpiryMs;
    private final long refreshTokenExpiryMs;

    public JwtUtil(
            @Value("${JWT_SECRET}") String secret,
            @Value("${jwt.access-expiry-ms:1209600000}") long accessTokenExpiryMs, //기본 15분 (900000) //todo : 현재 14일로 설정, 서비스시 변경 필요
            @Value("${jwt.refresh-expiry-ms:1209600000}") long refreshTokenExpiryMs // 기본 14일
    ) {
        byte[] keyBytes;
        try {
            // Prefer Base64-encoded secrets (common in env/secret managers)
            keyBytes = Decoders.BASE64.decode(secret);
        } catch (IllegalArgumentException ignore) {
            // Fallback: treat as raw text
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }

        // HS256 requires at least 256-bit (32-byte) key length
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("JWT secret is too short. Use at least 32 bytes (256-bit) for HS256.");
        }

        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpiryMs = accessTokenExpiryMs;
        this.refreshTokenExpiryMs = refreshTokenExpiryMs;
    }

    // 인증 인가에 사용 / subject : userId, claims : role , header : typ=JWT
    public String generateAccessToken(Long userId, String role) {
        return generateToken(userId, role, "ACCESS", accessTokenExpiryMs);
    }

    public String generateRefreshToken(Long userId) {
        return generateToken(userId, null, "REFRESH", refreshTokenExpiryMs);
    }

    private LocalDateTime expiredAtFrom(LocalDateTime issuedAt, long expiryMs) {
        return issuedAt.plusNanos(expiryMs * 1_000_000);
    }

    private String generateToken(Long userId, String role, String tokenType, long expiryMs) {
        LocalDateTime issuedAt = LocalDateTime.now();
        LocalDateTime expiredAt = expiredAtFrom(issuedAt, expiryMs);

        var builder = Jwts.builder()
                .header().type("JWT").and()
                .subject(userId.toString())
                .setIssuedAt(java.sql.Timestamp.valueOf(issuedAt))
                .setExpiration(java.sql.Timestamp.valueOf(expiredAt))
                .claim("tokenType", tokenType);
        if (role != null) {
            builder.claim("role", role);
        }

        return builder.signWith(key).compact();
    }

    /**
     * 토큰 파싱(검증 포함). 실패하면 empty.
     */
    public Optional<Claims> parseClaimsSafely(String token) {
        try {
            Jws<Claims> jws = Jwts.parser().verifyWith(key).build()
                    .parseSignedClaims(token);
            return Optional.of(jws.getPayload());
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public boolean isValid(String token) {
        return parseClaimsSafely(token).isPresent();
    }

    public Long getUserId(String token) {
        Claims claims = parseClaimsSafely(token)
                .orElseThrow(() -> new JwtException("Invalid token"));
        return Long.parseLong(claims.getSubject());
    }

    public Optional<String> getRole(String token) {
        return parseClaimsSafely(token).map(c -> c.get("role", String.class));
    }

    public Optional<String> getTokenType(String token) {
        return parseClaimsSafely(token).map(c -> c.get("tokenType", String.class));
    }

    public String getTokenTypeOrThrow(String token) {
        return getTokenType(token).orElseThrow(() -> new JwtException("Missing tokenType"));
    }

    public void assertAccessToken(String token) {
        String type = getTokenTypeOrThrow(token);
        if (!"ACCESS".equals(type)) {
            throw new JwtException("Not an access token");
        }
    }

    public void assertRefreshToken(String token) {
        String type = getTokenTypeOrThrow(token);
        if (!"REFRESH".equals(type)) {
            throw new JwtException("Not a refresh token");
        }
    }

    /**
     * DB에는 refreshToken 원문 저장 지양 -> 해시 저장 권장
     * - 운영에선 token 원문이 유출되면 즉시 세션 탈취 가능
     */
    public String hashRefreshToken(String rawRefreshToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(rawRefreshToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hashed);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256은 JVM에 기본 포함이라 사실상 발생하지 않지만, 명시적으로 처리
            throw new IllegalStateException("SHA-256 not supported", e);
        }
    }

    public LocalDateTime accessTokenExpiredAtFromNow() {
        return LocalDateTime.now().plusNanos(accessTokenExpiryMs * 1_000_000);
    }

    public LocalDateTime refreshTokenExpiredAtFromNow() {
        return LocalDateTime.now().plusNanos(refreshTokenExpiryMs * 1_000_000);
    }
}

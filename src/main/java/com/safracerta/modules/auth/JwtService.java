package com.safracerta.modules.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  private final SecretKey signingKey;
  private final long expirationMs;

  public JwtService(
      @Value("${jwt.secret}") String secret,
      @Value("${jwt.expiration-ms}") long expirationMs) {
    this.signingKey = buildKey(secret);
    this.expirationMs = expirationMs;
  }

  private static SecretKey buildKey(String secret) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] keyBytes = digest.digest(secret.getBytes(StandardCharsets.UTF_8));
      return Keys.hmacShaKeyFor(keyBytes);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    }
  }

  public String generateToken(Long userId, String email, boolean ativo) {
    Date now = new Date();
    Date exp = new Date(now.getTime() + expirationMs);
    return Jwts.builder()
        .subject(String.valueOf(userId))
        .claim("email", email)
        .claim("ativo", ativo)
        .issuedAt(now)
        .expiration(exp)
        .signWith(signingKey)
        .compact();
  }

  /**
   * Valida assinatura e expiração do JWT (uso típico em filtros de API).
   */
  public Claims parse(String token) {
    return Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload();
  }

  /**
   * Valida assinatura mas não rejeita token expirado. Use quando precisar ler claims
   * (ex.: identificar utilizador) sem bloquear por {@code exp} — por exemplo, ao comparar
   * com o token persistido em {@code usuario.autenticacao} depois de expirado, sem impedir
   * operações que não devem depender só da validade do JWT.
   */
  public Claims parseIgnoringExpiration(String token) {
    return Jwts.parser()
        .verifyWith(signingKey)
        .clock(() -> new Date(Long.MAX_VALUE))
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }
}

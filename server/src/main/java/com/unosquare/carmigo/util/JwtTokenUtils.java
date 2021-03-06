package com.unosquare.carmigo.util;

import com.unosquare.carmigo.exception.ExpiredJwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenUtils {

  @Value("${application.token.secret.key}")
  private String key;

  @Value("${application.token.expiration.in-hours}")
  private int hours;

  public String generateToken(final UserDetails userDetails) {
    final Map<String, Object> claims = new HashMap<>();
    return createToken(claims, userDetails.getUsername());
  }

  public Boolean validateToken(final String token, final UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }

  private String createToken(final Map<String, Object> claims, final String subject) {
    final Date now = Date.from(Instant.now());
    final Date inSuchHours = Date.from(now.toInstant().plus(Duration.ofHours(hours)));
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(now)
        .setExpiration(inSuchHours)
        .signWith(SignatureAlgorithm.HS256, key)
        .compact();
  }

  public String extractUsername(final String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public Date extractExpiration(final String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public <R> R extractClaim(final String token, final Function<Claims, R> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(final String token) {
    try {
      return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
    } catch (final io.jsonwebtoken.ExpiredJwtException ex) {
      throw new ExpiredJwtException("Expired JWT token");
    }
  }

  private Boolean isTokenExpired(final String token) {
    return extractExpiration(token).before(Date.from(Instant.now()));
  }
}

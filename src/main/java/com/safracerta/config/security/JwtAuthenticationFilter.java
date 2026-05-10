package com.safracerta.config.security;

import com.safracerta.modules.auth.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
  private static final String BEARER_PREFIX = "Bearer ";

  private final JwtService jwtService;

  public JwtAuthenticationFilter(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    String header = request.getHeader("Authorization");
    String uri = request.getMethod() + " " + request.getRequestURI();

    if (header == null || header.isBlank()) {
      log.debug("[JWT] {} sem header Authorization", uri);
    } else if (!header.startsWith(BEARER_PREFIX)) {
      log.warn("[JWT] {} header Authorization sem prefixo Bearer", uri);
    } else if (SecurityContextHolder.getContext().getAuthentication() != null) {
      log.debug("[JWT] {} ja autenticado, pulando", uri);
    } else {
      String token = header.substring(BEARER_PREFIX.length()).trim();
      try {
        Claims claims = jwtService.parse(token);
        Long userId = Long.parseLong(claims.getSubject());
        UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken(userId, null, List.of());
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(auth);
        log.info("[JWT] {} autenticado como userId={}", uri, userId);
      } catch (Exception e) {
        log.warn("[JWT] {} token invalido: {}", uri, e.getMessage());
      }
    }
    filterChain.doFilter(request, response);
  }
}

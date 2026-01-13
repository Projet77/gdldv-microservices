package com.gdldv.gateway.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    private SecretKey key;

    // Endpoints publics (pas besoin de JWT)
    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
            "/api/auth/login",
            "/api/auth/register",
            "/api/users/register",
            "/api/v1/vehicles",
            "/api/admin/**",
            "/api/superadmin/**",
            "/actuator/**");

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    private SecretKey getKey() {
        if (key == null) {
            key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        }
        return key;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();

            // LOGGING DEBUG
            String method = exchange.getRequest().getMethod().name();
            System.out.println("🔒 [Gateway] REQ: " + method + " " + path);

            // OPTIONS (CORS Preflight) is handled by CorsConfig, so we just skip auth
            if (method.equals("OPTIONS")) {
                System.out.println("✈️ [Gateway] Handling OPTIONS (CORS) for: " + path);
                return chain.filter(exchange);
            }

            // Skip public endpoints
            // Simple wildcard matching for /actuator/**
            if (PUBLIC_ENDPOINTS.stream().anyMatch(endpoint -> {
                if (endpoint.endsWith("/**")) {
                    return path.startsWith(endpoint.substring(0, endpoint.length() - 3));
                }
                return path.contains(endpoint);
            })) {
                System.out.println("🔓 [Gateway] Public Endpoint (Skipping Auth): " + path);
                return chain.filter(exchange);
            }

            // Get JWT token from header
            String token = getTokenFromHeader(exchange);
            System.out.println("🔑 [Gateway] Token present: " + (token != null));

            if (token == null || token.isEmpty()) {
                System.out.println("❌ [Gateway] No Token provided!");
                return onError(exchange, "No authorization token provided", HttpStatus.UNAUTHORIZED);
            }

            try {
                // Validate token using JJWT (Exact same logic as User Service)
                Jwts.parser()
                        .verifyWith(getKey())
                        .build()
                        .parseSignedClaims(token);

                System.out.println("✅ [Gateway] Token Validated. Forwarding request...");
                // Token valid, continue
                return chain.filter(exchange);
            } catch (JwtException e) {
                System.out.println("❌ [Gateway] JWT Validation Failed: " + e.getMessage());
                return onError(exchange, "Invalid JWT token: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
            } catch (Exception e) {
                System.out.println("❌ [Gateway] Token Validation Failed: " + e.getClass().getSimpleName());
                return onError(exchange, "JWT validation failed", HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private String getTokenFromHeader(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        return exchange.getResponse().setComplete();
    }

    public static class Config {
    }
}

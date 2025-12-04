package com.gdldv.gateway.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    // Endpoints publics (pas besoin de JWT)
    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
        "/api/auth/login",
        "/api/auth/register",
        "/api/users/register"
    );

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();

            // Skip public endpoints
            if (PUBLIC_ENDPOINTS.stream().anyMatch(path::contains)) {
                return chain.filter(exchange);
            }

            // Get JWT token from header
            String token = getTokenFromHeader(exchange);

            if (token == null || token.isEmpty()) {
                return onError(exchange, "No authorization token provided", HttpStatus.UNAUTHORIZED);
            }

            try {
                // Validate token
                Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token);

                // Token valid, continue
                return chain.filter(exchange);
            } catch (SignatureException e) {
                return onError(exchange, "Invalid JWT token", HttpStatus.UNAUTHORIZED);
            } catch (Exception e) {
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

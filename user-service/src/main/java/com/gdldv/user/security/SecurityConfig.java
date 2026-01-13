package com.gdldv.user.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Désactiver CSRF complètement pour simplifier

                // Utiliser IF_REQUIRED pour permettre les sessions pour les pages web
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                // Configurer le SecurityContextRepository pour sauvegarder dans la session
                .securityContext(context -> context.securityContextRepository(securityContextRepository()))
                .authorizeHttpRequests(auth -> auth
                        // Pages publiques
                        .requestMatchers("/", "/home", "/index", "/login", "/register", "/css/**", "/js/**",
                                "/images/**")
                        .permitAll()

                        // Endpoints API publics (authentification JWT)
                        .requestMatchers("/api/auth/**").permitAll()

                        // Swagger / OpenAPI
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // Actuator (health checks)
                        .requestMatchers("/actuator/**").permitAll()

                        // API check-out/check-in endpoints
                        .requestMatchers("/api/check-out/**", "/api/check-in/**", "/api/inspections/**").permitAll()

                        // Admin API endpoints (temporary: allow all during development)
                        .requestMatchers("/api/admin/**", "/api/superadmin/**").permitAll()

                        // Pages web protégées (nécessitent authentification)
                        .requestMatchers("/dashboard", "/logout", "/profile", "/profile/**", "/rental-history")
                        .authenticated()

                        // Admin pages (protégées)
                        .requestMatchers("/admin/**").hasAnyRole("ADMIN", "EMPLOYEE")

                        // Endpoints API protégés (nécessitent JWT)
                        .requestMatchers("/api/users/**").authenticated()

                        // Tous les autres endpoints nécessitent authentification
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email") // Utiliser 'email' au lieu de 'username'
                        .passwordParameter("password")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?error")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll())
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
package com.gdldv.user.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration du cache Redis
 * Utilisé pour améliorer les performances des dashboards
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // Configuration par défaut
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5)) // TTL par défaut: 5 minutes
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new StringRedisSerializer()))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();

        // Configurations spécifiques par cache
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // Dashboard client - 5 minutes
        cacheConfigurations.put("clientDashboard",
                defaultConfig.entryTtl(Duration.ofMinutes(5)));

        // Dashboard agent - 2 minutes (données plus dynamiques)
        cacheConfigurations.put("agentDashboard",
                defaultConfig.entryTtl(Duration.ofMinutes(2)));

        // Dashboard manager - 10 minutes
        cacheConfigurations.put("managerDashboard",
                defaultConfig.entryTtl(Duration.ofMinutes(10)));

        // Dashboard super admin - 15 minutes
        cacheConfigurations.put("superAdminDashboard",
                defaultConfig.entryTtl(Duration.ofMinutes(15)));

        // Favoris - 30 minutes
        cacheConfigurations.put("favorites",
                defaultConfig.entryTtl(Duration.ofMinutes(30)));

        // Statistiques véhicules - 1 heure
        cacheConfigurations.put("vehicleStats",
                defaultConfig.entryTtl(Duration.ofHours(1)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware()
                .build();
    }
}

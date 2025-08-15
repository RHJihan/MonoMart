package com.monomart.config;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration class to load environment variables from .env file
 * This ensures that .env files are loaded before Spring Boot processes application properties
 */
@Slf4j
@Configuration
public class DotEnvConfig {

    private final ConfigurableEnvironment environment;

    public DotEnvConfig(ConfigurableEnvironment environment) {
        this.environment = environment;
        loadDotEnv();
    }

    private void loadDotEnv() {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .directory(".")
                    .filename(".env")
                    .ignoreIfMalformed()
                    .ignoreIfMissing()
                    .load();

            Map<String, Object> envProperties = new HashMap<>();
            dotenv.entries().forEach(entry -> {
                String key = entry.getKey();
                String value = entry.getValue();
                
                // Only set if not already set by system environment
                if (System.getenv(key) == null) {
                    envProperties.put(key, value);
                    System.setProperty(key, value);
                }
            });

            if (!envProperties.isEmpty()) {
                MapPropertySource dotenvPropertySource = new MapPropertySource("dotenv", envProperties);
                environment.getPropertySources().addLast(dotenvPropertySource);
                log.info("Loaded {} properties from .env file", envProperties.size());
                log.debug("Loaded .env properties: {}", String.join(", ", envProperties.keySet()));
            } else {
                log.debug("No .env file found or no new properties to load");
            }

        } catch (DotenvException e) {
            log.warn("Failed to load .env file: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while loading .env file", e);
        }
    }

    @PostConstruct
    public void logEnvironmentInfo() {
        String activeProfiles = String.join(", ", environment.getActiveProfiles());
        if (activeProfiles.isEmpty()) {
            activeProfiles = String.join(", ", environment.getDefaultProfiles());
        }
        log.info("Active Spring profiles: {}", activeProfiles);
        
        // Log database connection info (without password)
        String dbUrl = environment.getProperty("spring.datasource.url", environment.getProperty("DB_URL", "not configured"));
        String dbUsername = environment.getProperty("spring.datasource.username", environment.getProperty("DB_USERNAME", "not configured"));
        log.info("Database URL: {}", dbUrl);
        log.info("Database Username: {}", dbUsername);
    }
}

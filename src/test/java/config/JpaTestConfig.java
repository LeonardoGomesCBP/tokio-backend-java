package config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;

@Configuration
@EnableAutoConfiguration
@EntityScan(basePackages = "entities")
@EnableJpaRepositories(basePackages = "repositories")
@EnableJpaAuditing(auditorAwareRef = "testAuditorProvider")
public class JpaTestConfig {
    
    @Bean
    public AuditorAware<String> testAuditorProvider() {
        return () -> Optional.of("test-user");
    }
} 
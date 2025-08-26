package com.monomart.bootstrap;

import com.monomart.entities.User;
import com.monomart.entities.enums.Role;
import com.monomart.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminSeeder {

    @Bean
    CommandLineRunner seedAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.existsByUsername("admin")) return;
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@monomart.local");
            admin.setPassword(passwordEncoder.encode("Admin@12345"));
            admin.setRole(Role.ROLE_ADMIN);
            userRepository.save(admin);
        };
    }
}



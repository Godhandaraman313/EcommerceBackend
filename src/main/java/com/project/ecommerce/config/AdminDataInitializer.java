package com.project.ecommerce.config;

import com.project.ecommerce.model.User;
import com.project.ecommerce.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AdminDataInitializer(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        String adminEmail = "admin@kaimart.com";

        userRepository.findByEmail(adminEmail).ifPresentOrElse(user -> {
            if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
                user.setRole("ADMIN");
                userRepository.save(user);
            }
        }, () -> {
            User admin = new User();
            admin.setEmail(adminEmail);
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFirstName("Shop");
            admin.setLastName("Admin");
            admin.setRole("ADMIN");
            userRepository.save(admin);
        });
    }
}

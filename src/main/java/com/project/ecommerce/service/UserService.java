package com.project.ecommerce.service;

import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.ecommerce.config.JwtUtil;
import com.project.ecommerce.dto.AuthResponse;
import com.project.ecommerce.model.User;
import com.project.ecommerce.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    // ================= REGISTER =================
    public User register(User user) {

        Optional<User> existing = repo.findByEmail(user.getEmail());

        if (existing.isPresent()) {
            throw new RuntimeException("User already exists");
        }

        user.setPassword(encoder.encode(user.getPassword()));
        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("CUSTOMER");
        }

        User savedUser = repo.save(user);

        // Send registration welcome email
        try {
            emailService.sendRegistrationEmail(savedUser.getEmail());
        } catch (Exception e) {
            // Don't fail registration if email sending fails
            System.err.println("Failed to send registration email: " + e.getMessage());
        }

        return savedUser;
    }

    // ================= LOGIN =================
    public AuthResponse login(String email, String password) {

        User user = repo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Not a Registered User"));

        if (!encoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid Password");
        }

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getUsername(),
                user.getRole()
        );

        return new AuthResponse(token, user.getUsername(), user.getEmail(), user.getRole());
    }

    public User getByEmail(String email) {
        return repo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void updateAccount(String email, User updated) {

        User user = repo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFirstName(updated.getFirstName());
        user.setLastName(updated.getLastName());
        user.setPhoneNumber(updated.getPhoneNumber());
        user.setAddressLine1(updated.getAddressLine1());
        user.setCity(updated.getCity());
        user.setCountry(updated.getCountry());
        user.setPostalCode(updated.getPostalCode());

        repo.save(user);
    }

    public void createPasswordResetTokenForUser(String email) {
        User user = repo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        String token = java.util.UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(java.time.LocalDateTime.now().plusHours(1));
        repo.save(user);

        try {
            emailService.sendPasswordResetEmail(user.getEmail(), token);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send password reset email: " + e.getMessage());
        }
    }

    public void resetPassword(String token, String newPassword) {
        User user = repo.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired password reset token"));

        if (user.getResetTokenExpiry() == null || java.time.LocalDateTime.now().isAfter(user.getResetTokenExpiry())) {
            throw new RuntimeException("Password reset token has expired");
        }

        user.setPassword(encoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        repo.save(user);
    }

    private String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder result = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            result.append(chars.charAt(random.nextInt(chars.length())));
        }
        return result.toString();
    }
}
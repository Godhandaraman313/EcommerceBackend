package com.project.ecommerce.service;

import java.util.Optional;

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

    // ================= REGISTER =================
    public User register(User user) {

        Optional<User> existing = repo.findByEmail(user.getEmail());

        if (existing.isPresent()) {
            throw new RuntimeException("User already exists");
        }

        user.setPassword(encoder.encode(user.getPassword()));

        return repo.save(user);
    }

    // ================= LOGIN =================
    public AuthResponse login(String email, String password) {

        User user = repo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Not a Registered User"));

        if (!encoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid Password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getUsername());

        return new AuthResponse(token, user.getUsername(), user.getEmail());
    }

    // ================= RESET PASSWORD =================
    public String resetPassword(String email, String newPassword) {

        User user = repo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not registered"));

        user.setPassword(encoder.encode(newPassword));

        repo.save(user);

        return "Password updated successfully";
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

        repo.save(user); // ✅ FIXED
    }
    
}
package com.project.ecommerce.controller;

import com.project.ecommerce.model.User;
import com.project.ecommerce.service.UserService;
import com.project.ecommerce.dto.AuthResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("http://localhost:5173")
public class AuthController {

    @Autowired
    private UserService service;

    // ================= REGISTER =================
    @PostMapping("/register")
    public String register(@RequestBody User user) {
        service.register(user);
        return "User registered successfully";
    }

    // ================= LOGIN =================
    @PostMapping("/login")
    public AuthResponse login(@RequestBody Map<String, String> request) {
        return service.login(
                request.get("email"),
                request.get("password")
        );
    }

    // ================= RESET PASSWORD =================
    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody Map<String, String> request) {
        return service.resetPassword(
                request.get("email"),
                request.get("password")
        );
    }
}
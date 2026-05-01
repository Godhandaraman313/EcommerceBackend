package com.project.ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.ecommerce.model.User;
import com.project.ecommerce.service.UserService;

@RestController
@RequestMapping("/api/account")
@CrossOrigin("http://localhost:5173")
public class AccountController {

    @Autowired
    private UserService userService;

    // ================= GET ACCOUNT =================
    @GetMapping
    public User getAccount(Authentication authentication) {
        String email = authentication.getName();
        return userService.getByEmail(email);
    }

    // ================= UPDATE ACCOUNT =================
    @PutMapping
    public String updateAccount(
            Authentication authentication,
            @RequestBody User updatedUser
    ) {
        String email = authentication.getName();
        userService.updateAccount(email, updatedUser);
        return "Account updated successfully";
    }
}
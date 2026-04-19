package com.project.ecommerce.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Application is running";
    }
    @GetMapping("/hii")
    public String hello() {
        return "<b>Hello<b>";
    }
}
package org.example.digibankjwt.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    @GetMapping("/profile")
    public String getCustomerProfile() {
        return "✅ Hello Customer! This is your profile.";    }
}

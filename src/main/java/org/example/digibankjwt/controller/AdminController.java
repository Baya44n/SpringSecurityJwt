package org.example.digibankjwt.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/manage-users")
    public String getCustomerProfile() {
        return "📊 Hello Admin! you can manage the users";    }
}

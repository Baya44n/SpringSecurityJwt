package org.example.digibankjwt.controller;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/custom")
public class SharedController {

    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    @GetMapping("/shared-resources")
    public String adminAndCustomer() {
        return "Only (admins) and (customers) can see this.";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public String adminOnly() {
        return "🧑‍💼 Only (admins) can see this.";
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/customer")
    public String customerOnly() {
        return "🧑‍🦱 Only (customers) can see this.";
    }

}

package org.example.digibankjwt.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProtectedController {

    @GetMapping("/protected")
    public String getProtectedResource() {
        return "✅ Access granted: You reached a protected endpoint because your JWT was valid.";    }
}

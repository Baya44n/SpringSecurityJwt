package org.example.digibankjwt.controller;

import org.example.digibankjwt.config.JwtProperties;
import org.example.digibankjwt.entity.User;
import org.example.digibankjwt.security.JwtService;
import org.example.digibankjwt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired private UserService userService;
    @Autowired private JwtService jwtService;
    @Autowired private AuthenticationManager authManager;
    @Autowired private JwtProperties props;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        return userService.register(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user){
        authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        UserDetails userDetails = userService.loadUserByUsername(user.getEmail());

        String accessToken = jwtService.generateToken(userDetails, props.getExpiration(), false);
        String refreshToken = jwtService.generateToken(userDetails, props.getRefreshExpiration(), true);

        String role = jwtService.extractUserRole(accessToken);
        String msg = "Hello "+ (role.equals("ROLE_ADMIN") ? "Admin" : "Customer");

        return ResponseEntity.ok(Map.of("msg",msg,"accessToken", accessToken, "refreshToken", refreshToken));
    }


    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String,String> body){
        String refreshToken = body.get("refreshToken");
        String username = jwtService.extractUsername(refreshToken);
        UserDetails userDetails = userService.loadUserByUsername(username);

        //  Check that it's really a refresh token
        String type = jwtService.extractTokenType(refreshToken);
        if (!"refresh".equals(type)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token type");
        }

        if (jwtService.validateToken(refreshToken, userDetails)) {
            String newAccessToken = jwtService.generateToken(userDetails, props.getExpiration(), false);
            return ResponseEntity.ok(Map.of(
                    "accessToken", newAccessToken,
                    "refreshToken", refreshToken
            ));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
    }


}

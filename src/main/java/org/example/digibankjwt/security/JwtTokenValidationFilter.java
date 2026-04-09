package org.example.digibankjwt.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.digibankjwt.service.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtTokenValidationFilter extends OncePerRequestFilter {


    private final JwtService jwtService;
    private final UserService userService;

    public JwtTokenValidationFilter(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            String type = jwtService.extractTokenType(token);


            //Debug
            System.out.println("Authorization header: " + header);
            System.out.println("Token type: " + type);
            System.out.println("JWT role: " +  jwtService.extractUserRole(token));

            // Only allow access tokens
            if ("access".equals(type)) {
                String username = jwtService.extractUsername(token);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userService.loadUserByUsername(username);
                    if (jwtService.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            }
        }
        chain.doFilter(request, response);
    }

}

package com.menuwise.auth.service;

import com.menuwise.auth.domain.User;
import com.menuwise.auth.dto.AuthResponse;
import com.menuwise.auth.dto.LoginRequest;
import com.menuwise.auth.dto.UserProfileDto;
import com.menuwise.auth.repository.UserRepository;
import com.menuwise.auth.security.JwtAuthFilter;
import com.menuwise.auth.security.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse login(LoginRequest request, HttpServletResponse response) {
        // Authenticate credentials against UserDetailsService and BCrypt
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + request.getUsername()));

        // Generate signed JWT
        String token = jwtService.generateToken(user.getUsername(), user.getRole().name(), user.getFullName());

        // Set HttpOnly cookie for seamless browser navigation & API requests
        ResponseCookie cookie = ResponseCookie.from(JwtAuthFilter.JWT_COOKIE_NAME, token)
                .httpOnly(true)
                .secure(false) // false allows local HTTP testing; true in production with HTTPS
                .path("/")
                .maxAge(Duration.ofDays(1))
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .build();
    }

    public void logout(HttpServletResponse response) {
        // Clear HttpOnly cookie
        ResponseCookie cookie = ResponseCookie.from(JwtAuthFilter.JWT_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        SecurityContextHolder.clearContext();
    }

    public UserProfileDto getCurrentUserProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }

        User user = userRepository.findByUsername(auth.getName())
                .orElse(null);

        if (user == null) return null;

        return UserProfileDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .build();
    }
}

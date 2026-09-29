package com.menuwise.auth.controller;

import com.menuwise.auth.domain.Role;
import com.menuwise.auth.domain.User;
import com.menuwise.auth.dto.CreateUserRequest;
import com.menuwise.auth.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminUserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String usersPage(Model model) {
        List<User> users = userRepository.findAll();
        long adminCount = users.stream().filter(u -> u.getRole() == Role.ROLE_ADMIN).count();
        long managerCount = users.stream().filter(u -> u.getRole() == Role.ROLE_MANAGER).count();
        long cashierCount = users.stream().filter(u -> u.getRole() == Role.ROLE_CASHIER).count();

        model.addAttribute("users", users);
        model.addAttribute("totalCount", users.size());
        model.addAttribute("adminCount", adminCount);
        model.addAttribute("managerCount", managerCount);
        model.addAttribute("cashierCount", cashierCount);
        model.addAttribute("activeRoute", "admin-users");
        return "pages/admin-users";
    }

    @PostMapping("/api/create")
    @ResponseBody
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Username already exists"));
        }

        User user = User.builder()
                .username(request.getUsername().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName().trim())
                .role(request.getRole())
                .build();

        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Staff account created successfully"));
    }

    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteUser(@PathVariable Long id, Authentication auth) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        if (user.getUsername().equalsIgnoreCase(auth.getName())) {
            return ResponseEntity.badRequest().body(Map.of("message", "You cannot delete your own account"));
        }

        userRepository.delete(user);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }
}

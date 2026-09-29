package com.menuwise.auth.controller;

import com.menuwise.auth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class LoginViewController {

    private final AuthService authService;

    @GetMapping("/login")
    public String loginPage(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model
    ) {
        if (logout != null) {
            model.addAttribute("successMessage", "You have been signed out successfully.");
            return "pages/login";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            // Already logged in, redirect to dashboard or appropriate home
            return "redirect:/dashboard";
        }

        if ("denied".equals(error)) {
            model.addAttribute("errorMessage", "Access Denied: Your account role does not have permission for that page.");
        }

        return "pages/login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        authService.logout(response);
        return "redirect:/login";
    }
}

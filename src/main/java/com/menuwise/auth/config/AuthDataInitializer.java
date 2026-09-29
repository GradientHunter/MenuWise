package com.menuwise.auth.config;

import com.menuwise.auth.domain.Role;
import com.menuwise.auth.domain.User;
import com.menuwise.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Ensure chef login is removed
        userRepository.findByUsername("chef").ifPresent(userRepository::delete);

        if (userRepository.count() == 0) {
            log.info("No users found in database. Seeding demo accounts for MenuWise...");

            List<User> demoUsers = List.of(
                    User.builder()
                            .username("admin")
                            .password(passwordEncoder.encode("admin123"))
                            .fullName("Syed Nahian")
                            .role(Role.ROLE_ADMIN)
                            .build(),

                    User.builder()
                            .username("manager")
                            .password(passwordEncoder.encode("manager123"))
                            .fullName("Farhan (Manager)")
                            .role(Role.ROLE_MANAGER)
                            .build(),

                    User.builder()
                            .username("cashier")
                            .password(passwordEncoder.encode("cashier123"))
                            .fullName("Rahim (Front Desk)")
                            .role(Role.ROLE_CASHIER)
                            .build()
            );

            userRepository.saveAll(demoUsers);
            log.info("Demo accounts seeded successfully: [admin, manager, cashier] (password: <username>123)");
        }
    }
}

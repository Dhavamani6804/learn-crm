package com.dhava.crmdemo.config;

import com.dhava.crmdemo.entity.User;
import com.dhava.crmdemo.enums.Role;
import com.dhava.crmdemo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SuperAdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${security.super-admin.name}")
    private String name;

    @Value("${security.super-admin.email}")
    private String email;

    @Value("${security.super-admin.phone}")
    private String phone;

    @Value("${security.super-admin.password}")
    private String password;

    @Override
    public void run(String @NonNull ... args) {

        if (userRepository.existsByRole(Role.SUPER_ADMIN)) {
            return;
        }

        User superAdmin = new User();

        superAdmin.setName(name);
        superAdmin.setEmail(email);
        superAdmin.setPhone(phone);
        superAdmin.setPasswordHash(passwordEncoder.encode(password));
        superAdmin.setRole(Role.SUPER_ADMIN);
        superAdmin.setIsActive(true);

        userRepository.save(superAdmin);

       log.info("SUPER_ADMIN account initialized successfully.");
    }
}
package com.adama.backoffice.dataDemoInit;

import com.adama.backoffice.users.entity.User;
import com.adama.backoffice.users.repository.UserRepository;
import java.util.Optional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        String adminString = "admin";
        String managerString = "manager";
        User.Role adminRole = User.Role.ROLE_ADMIN;
        User.Role managerRole = User.Role.ROLE_MANAGER;

        Optional<User> existingAdmin = userRepository.findByUsername(adminString);
        Optional<User> existingManager = userRepository.findByUsername(managerString);

        if (existingAdmin.isEmpty()) {
            User admin = new User();
            admin.setUsername(adminString);
            admin.setPassword(passwordEncoder.encode(adminString));
            admin.setRole(adminRole);

            userRepository.save(admin);
            System.out.println("✅ Usuario admin creado automáticamente");
        }
        if (existingManager.isEmpty()) {
            User manager = new User();
            manager.setUsername(managerString);
            manager.setPassword(managerString);
            manager.setRole(managerRole);

            userRepository.save(manager);
            System.out.println("✅ Usuario manager creado automáticamente");
        }
    }
}

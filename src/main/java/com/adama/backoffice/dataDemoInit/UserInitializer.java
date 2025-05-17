package com.adama.backoffice.dataDemoInit;

import com.adama.backoffice.users.entity.User;
import com.adama.backoffice.users.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        String adminString = "admin";
        String managerString = "manager";
        String warehouseString = "warehouse";
        String userString = "user";

        if (userRepository.findByUsername(adminString).isEmpty()) {
            createTestUser(adminString);
            System.out.println("✅ Usuario admin creado automáticamente");
        }
        if (userRepository.findByUsername(managerString).isEmpty()) {
            createTestUser(managerString);
            System.out.println("✅ Usuario manager creado automáticamente");
        }
        if (userRepository.findByUsername(warehouseString).isEmpty()) {
            createTestUser(warehouseString);
            System.out.println("✅ Usuario warehouse creado automáticamente");
        }
        if (userRepository.findByUsername(userString).isEmpty()) {
            createTestUser(userString);
            System.out.println("✅ Usuario user creado automaticamente");
        }
    }

    private void createTestUser(String string) {
        User newUser = new User();
        newUser.setUsername(string);
        newUser.setPassword(passwordEncoder.encode(string));
        newUser.setFirstName(string);
        newUser.setLastName(string);
        newUser.setRole(User.Role.valueOf("ROLE_" + string.toUpperCase()));
        newUser.setManagerUsername("manager");

        userRepository.save(newUser);
    }
}

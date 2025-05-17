package com.adama.backoffice.users.api;

import com.adama.backoffice.security.service.JwtService;
import com.adama.backoffice.users.entity.User;
import com.adama.backoffice.users.mapper.UserMapper;
import com.adama.backoffice.users.repository.UserRepository;
import com.adama.user.api.UserApi;
import com.adama.user.model.*;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
public class UserController implements UserApi {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    @PostMapping("/users")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest userRequest) {

        User user = UserMapper.toEntity(userRequest);
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user = userRepository.save(user);
        UserResponse response = UserMapper.toResponse(user);
        return ResponseEntity.status(201).body(response);
    }

    @Override
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        try {
            UUID uuid = UUID.fromString(id);
            if (userRepository.existsById(uuid)) {
                userRepository.deleteById(uuid);
            }
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<List<UserResponse>> getAllUsers(@RequestParam(required = false) String role) {
        List<User> users;

        if (role != null) {
            try {
                User.Role roleEnum = User.Role.valueOf(role);
                users = userRepository.findByRole(roleEnum);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        } else {
            users = userRepository.findAll();
        }

        List<UserResponse> response = users.stream().map(UserMapper::toResponse).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/myid")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> getMyId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // este es el username del token

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return ResponseEntity.ok(user.getId().toString());
    }

    @GetMapping("/users/myManager")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> getMyManager() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return ResponseEntity.ok(user.getManagerUsername());
    }

    @Override
    @GetMapping("/users/usernames")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<String>> getAllUsernames() {
        List<String> usernames = userRepository.findAllUsernames();
        return ResponseEntity.ok(usernames);
    }

    @Override
    @GetMapping("/users/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
        try {
            UUID uuid = UUID.fromString(id);
            Optional<User> optionalUser = userRepository.findById(uuid);

            if (optionalUser.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            User user = optionalUser.get();
            return ResponseEntity.ok(UserMapper.toResponse(user));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    @GetMapping("/users/username/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return ResponseEntity.ok(UserMapper.toResponse(user));
    }

    @Override
    @PostMapping("/auth/login")
    public ResponseEntity<Login200Response> login(@Valid @RequestBody LoginRequest loginRequest) {
        Optional<User> optionalUser = userRepository.findByUsername(loginRequest.getUsername());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = optionalUser.get();

        // 2. Verificar contraseña
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = jwtService.generateToken(user);

        Login200Response response = new Login200Response().token(token);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/users/role")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> getMyRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new IllegalStateException("User role not found"));

        return ResponseEntity.ok(role);
    }

    @Override
    @PatchMapping("/users/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable("id") String id, @Valid @RequestBody UserPatchRequest userPatchRequest) {
        try {
            UUID uuid = UUID.fromString(id);
            Optional<User> optionalUser = userRepository.findById(uuid);
            if (optionalUser.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            User user = optionalUser.get();
            if (userPatchRequest.getFirstName() != null) user.setFirstName(userPatchRequest.getFirstName());
            if (userPatchRequest.getLastName() != null) user.setLastName(userPatchRequest.getLastName());
            if (userPatchRequest.getDepartment() != null) user.setDepartment(userPatchRequest.getDepartment());
            if (userPatchRequest.getRole() != null) user.setRole(User.Role.valueOf(userPatchRequest.getRole()));
            if (userPatchRequest.getManagerUsername() != null)
                user.setManagerUsername(userPatchRequest.getManagerUsername());

            user.setLastModified(LocalDateTime.now().toString());
            user.setModifiedBy("SYSTEM");

            userRepository.save(user);
            UserResponse response = UserMapper.toResponse(user);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

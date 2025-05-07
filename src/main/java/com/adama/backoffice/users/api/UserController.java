package com.adama.backoffice.users.api;

import com.adama.backoffice.products.mapper.ProductMapper;
import com.adama.backoffice.users.entity.User;
import com.adama.backoffice.users.mapper.UserMapper;
import com.adama.backoffice.users.repository.UserRepository;
import com.adama.user.api.UserApi;
import com.adama.user.model.*;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class UserController implements UserApi {
    private final UserRepository userRepository;
    //private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public ResponseEntity<UserResponse> createUser(
            @Parameter(name = "UserRequest", description = "", required = true)
            @Valid @RequestBody UserRequest userRequest) {
        User user = UserMapper.toEntity(userRequest);
        user = userRepository.save(user);
        UserResponse response = UserMapper.toResponse(user);
        return ResponseEntity.status(201).body(response);
    }


    @Override
    public ResponseEntity<Void> deleteUser(String id) {
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
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users.stream()
                .map(UserMapper::toResponse)
                .collect(Collectors.toList()));
    }

    @Override
    public ResponseEntity<UserResponse> getUserById(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            Optional<User> optionalUser = userRepository.findById(uuid);

            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                return ResponseEntity.ok(UserMapper.toResponse(user));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<Login200Response> login(LoginRequest loginRequest) {
        return null;
    }

    @Override
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable("id") String id,
            @Valid @RequestBody UserPatchRequest userPatchRequest) {
        try{
            UUID uuid = UUID.fromString(id);
            Optional<User> optionalUser = userRepository.findById(uuid);
            if (optionalUser.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            User user = optionalUser.get();
            if(userPatchRequest.getFirstName() != null)user.setFirstName(userPatchRequest.getFirstName());
            if(userPatchRequest.getLastName() != null)user.setLastName(userPatchRequest.getLastName());
            if(userPatchRequest.getDepartment() != null)user.setDepartment(userPatchRequest.getDepartment());
            if(userPatchRequest.getRole() != null)user.setRole(userPatchRequest.getRole());
            if (userPatchRequest.getSupervisorId() != null)user.setSupervisorId(userPatchRequest.getSupervisorId());

            user.setLastModified(LocalDateTime.now());
            user.setModifiedBy("SYSTEM");

            userRepository.save(user);
            UserResponse response = UserMapper.toResponse(user);
            return ResponseEntity.ok(response);
    }catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().build();}
    }
}


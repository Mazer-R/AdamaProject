package com.adama.backoffice.users.mapper;

import com.adama.backoffice.users.entity.User;
import com.adama.user.model.UserRequest;
import com.adama.user.model.UserResponse;

public class UserMapper {

    public static User toEntity(UserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDepartment(request.getDepartment());
        user.setRole(User.Role.valueOf(request.getRole().name())); // Conversión a Enum
        return user;
    }

    public static UserResponse toResponse(User entity) {
        UserResponse response = new UserResponse();
        response.setId(entity.getId().toString());
        response.setUsername(entity.getUsername());
        response.setFirstName(entity.getFirstName());
        response.setLastName(entity.getLastName());
        response.setDepartment(entity.getDepartment());
        response.setSupervisorId(entity.getSupervisorId());
        response.setRole(entity.getRole().name());
        if (entity.getCreated() != null)
            response.setCreated(entity.getCreated());
        if (entity.getLastModified() != null) {
            response.setLastModified(entity.getLastModified());
        }
        response.setModifiedBy(entity.getModifiedBy());
        return response;
    }
    }

package com.adama.backoffice.users.mapper;

import com.adama.backoffice.users.entity.User;
import com.adama.user.model.UserRequest;
import com.adama.user.model.UserResponse;

import java.time.LocalDateTime;

public class UserMapper {

    public static User toEntity(UserRequest request){
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDepartment(request.getDepartment());
        user.setRole(request.getRole().toString());
        user.setSupervisorId(request.getSupervisorId());
        user.setCreated(LocalDateTime.now());
        user.setLastModified(LocalDateTime.now());
        user.setModifiedBy("SYSTEM");
        return user;
    }

    public static UserResponse toResponse(User entitiy){
        UserResponse response = new UserResponse();
        response.setFirstName(entitiy.getFirstName());
        response.setLastName(entitiy.getLastName());
        response.setDepartment(entitiy.getDepartment());
        response.setRole(entitiy.getRole().toString());
        response.setSupervisorId(entitiy.getSupervisorId());
        return response;
    }
}

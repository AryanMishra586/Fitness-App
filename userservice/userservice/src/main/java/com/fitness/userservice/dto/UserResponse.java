package com.fitness.userservice.dto;
import com.fitness.userservice.models.UserRole;

import lombok.Data;

@Data
public class UserResponse {

    private String id;
    private String firstName;
    private String lastName;
    private UserRole role=UserRole.USER;
}

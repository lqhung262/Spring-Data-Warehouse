package com.example.demo.dto.systemuser.User;

import com.example.demo.dto.systemuser.Role.RoleResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    Long userId;
    String authorizationServiceUserId;
    String userName;
    String fullName;
    String emailAddress;
    Set<RoleResponse> roles;
}

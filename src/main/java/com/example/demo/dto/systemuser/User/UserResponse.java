package com.example.demo.dto.systemuser.User;

import lombok.*;
import lombok.experimental.FieldDefaults;

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
}

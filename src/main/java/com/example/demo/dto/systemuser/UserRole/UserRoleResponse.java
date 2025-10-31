package com.example.demo.dto.systemuser.UserRole;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRoleResponse {
    Long userRoleId;
    Long userId;
    Long roleId;
}

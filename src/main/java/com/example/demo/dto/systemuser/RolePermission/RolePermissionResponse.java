package com.example.demo.dto.systemuser.RolePermission;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RolePermissionResponse {
    Long rolePermissionId;
    Long roleId;
    Long permissionId;
}

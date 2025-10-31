package com.example.demo.dto.systemuser.RolePermission;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolePermissionRequest {
    @NotNull
    private Long roleId;

    @NotNull
    private Long permissionId;

}

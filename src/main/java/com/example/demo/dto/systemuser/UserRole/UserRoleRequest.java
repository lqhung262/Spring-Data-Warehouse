package com.example.demo.dto.systemuser.UserRole;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRoleRequest {
    @NotNull
    private Long userId;

    @NotNull
    private Long roleId;

}

package com.example.demo.dto.systemuser.Role;

import com.example.demo.entity.systemuser.Permission;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleRequest {
    @NotNull
    private String shortName;

    @NotBlank
    private String description;

    @NotBlank
    private String note;

    private Set<Permission> permissions;
}

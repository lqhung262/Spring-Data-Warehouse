package com.example.demo.dto.systemuser.Role;

import com.example.demo.entity.systemuser.Permission;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleResponse {
    Long roleId;
    String shortName;
    String description;
    String note;
    Set<Permission> permissions;

}

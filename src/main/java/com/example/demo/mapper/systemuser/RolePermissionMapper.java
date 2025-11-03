package com.example.demo.mapper.systemuser;

import com.example.demo.dto.systemuser.RolePermission.RolePermissionRequest;
import com.example.demo.dto.systemuser.RolePermission.RolePermissionResponse;
import com.example.demo.entity.systemuser.Permission;
import com.example.demo.entity.systemuser.Role;
import com.example.demo.entity.systemuser.RolePermission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RolePermissionMapper {
    @Mapping(source = "roleId", target = "roleId")
    @Mapping(source = "permissionId", target = "permissionId")
    RolePermission toRolePermission(RolePermissionRequest request);

    @Mapping(source = "roleId.roleId", target = "roleId")
    @Mapping(source = "permissionId.permissionId", target = "permissionId")
    RolePermissionResponse toRolePermissionResponse(RolePermission rolePermission);

    void updateRolePermission(@MappingTarget RolePermission rolePermission, RolePermissionRequest request);

    // helper methods for MapStruct to convert between id and entity
    default Role mapRole(Long roleId) {
        if (roleId == null) return null;
        Role r = new Role();
        r.setRoleId(roleId);
        return r;
    }

    default Permission mapPermission(Long permissionId) {
        if (permissionId == null) return null;
        Permission p = new Permission();
        p.setPermissionId(permissionId);
        return p;
    }

    default Long map(Role role) {
        return role == null ? null : role.getRoleId();
    }

    default Long map(Permission permission) {
        return permission == null ? null : permission.getPermissionId();
    }
}

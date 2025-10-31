package com.example.demo.mapper.systemuser;

import com.example.demo.dto.systemuser.RolePermission.RolePermissionRequest;
import com.example.demo.dto.systemuser.RolePermission.RolePermissionResponse;
import com.example.demo.entity.systemuser.RolePermission;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RolePermissionMapper {
    RolePermission toRolePermission(RolePermissionRequest request);

    RolePermissionResponse toRolePermissionResponse(RolePermission rolePermission);

    void updateRolePermission(@MappingTarget RolePermission rolePermission, RolePermissionRequest request);
}

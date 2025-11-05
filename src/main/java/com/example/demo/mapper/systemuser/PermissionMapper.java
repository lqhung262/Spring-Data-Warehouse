package com.example.demo.mapper.systemuser;

import com.example.demo.dto.systemuser.Permission.PermissionRequest;
import com.example.demo.dto.systemuser.Permission.PermissionResponse;
import com.example.demo.entity.systemuser.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PermissionMapper {
    PermissionResponse toPermissionResponse(Permission permission);

    Permission toPermission(PermissionRequest request);

    void updatePermission(@MappingTarget Permission permission, PermissionRequest request);
}

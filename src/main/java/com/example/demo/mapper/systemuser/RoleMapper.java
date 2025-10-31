package com.example.demo.mapper.systemuser;

import com.example.demo.dto.systemuser.Role.RoleRequest;
import com.example.demo.dto.systemuser.Role.RoleResponse;
import com.example.demo.entity.systemuser.Role;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoleMapper {
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);

    void updateRole(@MappingTarget Role role, RoleRequest request);
}

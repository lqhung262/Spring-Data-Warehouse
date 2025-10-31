package com.example.demo.mapper.systemuser;

import com.example.demo.dto.systemuser.UserRole.UserRoleRequest;
import com.example.demo.dto.systemuser.UserRole.UserRoleResponse;
import com.example.demo.entity.systemuser.UserRole;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserRoleMapper {
    UserRole toUserRole(UserRoleRequest request);

    UserRoleResponse toUserRoleResponse(UserRole userRole);

    void updateUserRole(@MappingTarget UserRole userRole, UserRoleRequest request);
}

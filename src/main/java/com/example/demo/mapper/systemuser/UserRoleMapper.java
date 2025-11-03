package com.example.demo.mapper.systemuser;

import com.example.demo.dto.systemuser.UserRole.UserRoleRequest;
import com.example.demo.dto.systemuser.UserRole.UserRoleResponse;
import com.example.demo.entity.systemuser.Role;
import com.example.demo.entity.systemuser.User;
import com.example.demo.entity.systemuser.UserRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserRoleMapper {
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "roleId", target = "roleId")
    UserRole toUserRole(UserRoleRequest request);

    // map from entity to response: entity.user.userId -> response.userId
    @Mapping(source = "userId.userId", target = "userId")
    @Mapping(source = "roleId.roleId", target = "roleId")
    UserRoleResponse toUserRoleResponse(UserRole userRole);

    void updateUserRole(@MappingTarget UserRole userRole, UserRoleRequest request);

    // helper methods for MapStruct to convert between id and entity
    default User map(Long userId) {
        if (userId == null) return null;
        User u = new User();
        u.setUserId(userId);
        return u;
    }

    default Role mapRole(Long roleId) {
        if (roleId == null) return null;
        Role r = new Role();
        r.setRoleId(roleId);
        return r;
    }

    default Long map(User user) {
        return user == null ? null : user.getUserId();
    }

    default Long map(Role role) {
        return role == null ? null : role.getRoleId();
    }
}

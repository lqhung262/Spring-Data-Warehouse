package com.example.demo.service.systemuser;

import com.example.demo.dto.systemuser.Role.RoleRequest;
import com.example.demo.dto.systemuser.Role.RoleResponse;
import com.example.demo.entity.systemuser.Role;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.systemuser.RoleMapper;
import com.example.demo.repository.systemuser.RolePermissionRepository;
import com.example.demo.repository.systemuser.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleService {
    final RoleRepository roleRepository;
    final RoleMapper roleMapper;
    final RolePermissionRepository rolePermissionRepository;

    @Value("${entities.systemuser.role}")
    private String entityName;

    public RoleResponse createRole(RoleRequest request) {
        Role role = roleMapper.toRole(request);

        Role saved = roleRepository.save(role);
        RoleResponse resp = roleMapper.toRoleResponse(saved);
        // populate permissions if any
        var perms = rolePermissionRepository.findByRoleId(saved).stream()
                .map(rp -> rp.getPermissionId())
                .map(p -> com.example.demo.dto.systemuser.Permission.PermissionResponse.builder()
                        .permissionId(p.getPermissionId())
                        .shortName(p.getShortName())
                        .description(p.getDescription())
                        .url(p.getUrl())
                        .method(p.getMethod())
                        .isPublic(p.getIsPublic())
                        .build())
                .collect(Collectors.toSet());
        resp.setPermissions(perms);
        return resp;
    }

    public List<RoleResponse> getRoles(Pageable pageable) {
        Page<Role> page = roleRepository.findAll(pageable);
        return page.getContent()
                .stream()
                .map(role -> {
                    RoleResponse resp = roleMapper.toRoleResponse(role);
                    var perms = rolePermissionRepository.findByRoleId(role).stream()
                            .map(rp -> rp.getPermissionId())
                            .map(p -> com.example.demo.dto.systemuser.Permission.PermissionResponse.builder()
                                    .permissionId(p.getPermissionId())
                                    .shortName(p.getShortName())
                                    .description(p.getDescription())
                                    .url(p.getUrl())
                                    .method(p.getMethod())
                                    .isPublic(p.getIsPublic())
                                    .build())
                            .collect(Collectors.toSet());
                    resp.setPermissions(perms);
                    return resp;
                }).toList();
    }

    public RoleResponse getRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        RoleResponse resp = roleMapper.toRoleResponse(role);
        var perms = rolePermissionRepository.findByRoleId(role).stream()
                .map(rp -> rp.getPermissionId())
                .map(p -> com.example.demo.dto.systemuser.Permission.PermissionResponse.builder()
                        .permissionId(p.getPermissionId())
                        .shortName(p.getShortName())
                        .description(p.getDescription())
                        .url(p.getUrl())
                        .method(p.getMethod())
                        .isPublic(p.getIsPublic())
                        .build())
                .collect(Collectors.toSet());
        resp.setPermissions(perms);
        return resp;
    }

    public RoleResponse updateRole(Long id, RoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        roleMapper.updateRole(role, request);

        return roleMapper.toRoleResponse(roleRepository.save(role));
    }

    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        roleRepository.deleteById(id);
    }
}

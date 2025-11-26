package com.example.demo.service.systemuser;

import com.example.demo.dto.systemuser.RolePermission.RolePermissionRequest;
import com.example.demo.dto.systemuser.RolePermission.RolePermissionResponse;
import com.example.demo.entity.systemuser.RolePermission;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.systemuser.RolePermissionMapper;
import com.example.demo.repository.systemuser.RolePermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RolePermissionService {
    final RolePermissionRepository rolePermissionRepository;
    final RolePermissionMapper rolePermissionMapper;

    @Value("${entities.systemuser.rolepermission}")
    private String entityName;

    public RolePermissionResponse createRolePermission(RolePermissionRequest request) {
        RolePermission rolePermission = rolePermissionMapper.toRolePermission(request);

        return rolePermissionMapper.toRolePermissionResponse(rolePermissionRepository.save(rolePermission));
    }

    public List<RolePermissionResponse> getRolePermissions(Pageable pageable) {
        Page<RolePermission> page = rolePermissionRepository.findAll(pageable);
        return page.getContent()
                .stream().map(rolePermissionMapper::toRolePermissionResponse).toList();
    }

    public RolePermissionResponse getRolePermission(Long id) {
        return rolePermissionMapper.toRolePermissionResponse(rolePermissionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public RolePermissionResponse updateRolePermission(Long id, RolePermissionRequest request) {
        RolePermission rolePermission = rolePermissionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        rolePermissionMapper.updateRolePermission(rolePermission, request);

        return rolePermissionMapper.toRolePermissionResponse(rolePermissionRepository.save(rolePermission));
    }

    public void deleteRolePermission(Long id) {
        if (!rolePermissionRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        rolePermissionRepository.deleteById(id);
    }
}

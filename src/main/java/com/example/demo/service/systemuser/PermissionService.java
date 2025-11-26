package com.example.demo.service.systemuser;

import com.example.demo.dto.systemuser.Permission.PermissionRequest;
import com.example.demo.dto.systemuser.Permission.PermissionResponse;
import com.example.demo.entity.systemuser.Permission;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.systemuser.PermissionMapper;
import com.example.demo.repository.systemuser.PermissionRepository;
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
public class PermissionService {
    final PermissionRepository permissionRepository;
    final PermissionMapper permissionMapper;

    @Value("${entities.systemuser.permission}")
    private String entityName;

    public PermissionResponse createPermission(PermissionRequest request) {
        Permission permission = permissionMapper.toPermission(request);

        return permissionMapper.toPermissionResponse(permissionRepository.save(permission));
    }

    public List<PermissionResponse> getPermissions(Pageable pageable) {
        Page<Permission> page = permissionRepository.findAll(pageable);
        return page.getContent()
                .stream().map(permissionMapper::toPermissionResponse).toList();
    }

    public PermissionResponse getPermission(Long id) {
        return permissionMapper.toPermissionResponse(permissionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public PermissionResponse updatePermission(Long id, PermissionRequest request) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        permissionMapper.updatePermission(permission, request);

        return permissionMapper.toPermissionResponse(permissionRepository.save(permission));
    }

    public void deletePermission(Long id) {
        if (!permissionRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        permissionRepository.deleteById(id);
    }
}

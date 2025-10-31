package com.example.demo.service.systemuser;

import com.example.demo.dto.systemuser.Role.RoleRequest;
import com.example.demo.dto.systemuser.Role.RoleResponse;
import com.example.demo.entity.systemuser.Role;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.systemuser.RoleMapper;
import com.example.demo.repository.systemuser.RoleRepository;
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
public class RoleService {
    final RoleRepository roleRepository;
    final RoleMapper roleMapper;

    @Value("${entities.systemuser.role}")
    private String entityName;

    public RoleResponse createRole(RoleRequest request) {
        Role role = roleMapper.toRole(request);

        return roleMapper.toRoleResponse(roleRepository.save(role));
    }

    public List<RoleResponse> getRoles(Pageable pageable) {
        Page<Role> page = roleRepository.findAll(pageable);
        return page.getContent()
                .stream().map(roleMapper::toRoleResponse).toList();
    }

    public RoleResponse getRole(Long id) {
        return roleMapper.toRoleResponse(roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public RoleResponse updateRole(Long id, RoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        roleMapper.updateRole(role, request);

        return roleMapper.toRoleResponse(roleRepository.save(role));
    }

    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        roleRepository.deleteById(id);
    }
}

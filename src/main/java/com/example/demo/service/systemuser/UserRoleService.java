package com.example.demo.service.systemuser;

import com.example.demo.dto.systemuser.UserRole.UserRoleRequest;
import com.example.demo.dto.systemuser.UserRole.UserRoleResponse;
import com.example.demo.entity.systemuser.UserRole;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.systemuser.UserRoleMapper;
import com.example.demo.repository.systemuser.UserRoleRepository;
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
public class UserRoleService {
    final UserRoleRepository userRoleRepository;
    final UserRoleMapper userRoleMapper;

    @Value("${entities.systemuser.userrole}")
    private String entityName;

    public UserRoleResponse createUserRole(UserRoleRequest request) {
        UserRole userRole = userRoleMapper.toUserRole(request);

        return userRoleMapper.toUserRoleResponse(userRoleRepository.save(userRole));
    }

    public List<UserRoleResponse> getUserRoles(Pageable pageable) {
        Page<UserRole> page = userRoleRepository.findAll(pageable);
        return page.getContent()
                .stream().map(userRoleMapper::toUserRoleResponse).toList();
    }

    public UserRoleResponse getUserRole(Long id) {
        return userRoleMapper.toUserRoleResponse(userRoleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public UserRoleResponse updateUserRole(Long id, UserRoleRequest request) {
        UserRole userRole = userRoleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        userRoleMapper.updateUserRole(userRole, request);

        return userRoleMapper.toUserRoleResponse(userRoleRepository.save(userRole));
    }

    public void deleteUserRole(Long id) {
        if (!userRoleRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        userRoleRepository.deleteById(id);
    }
}

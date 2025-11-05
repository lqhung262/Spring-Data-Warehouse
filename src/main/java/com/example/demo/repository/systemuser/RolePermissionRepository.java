package com.example.demo.repository.systemuser;

import com.example.demo.entity.systemuser.Role;
import com.example.demo.entity.systemuser.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    List<RolePermission> findByRoleId(Role role);
}


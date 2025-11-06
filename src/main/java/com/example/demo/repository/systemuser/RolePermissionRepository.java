package com.example.demo.repository.systemuser;

import com.example.demo.entity.systemuser.Permission;
import com.example.demo.entity.systemuser.Role;
import com.example.demo.entity.systemuser.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    List<RolePermission> findByRoleId(Role role);

    @Query("select rp.permissionId from RolePermission rp where rp.roleId.shortName = :shortName")
    List<Permission> findPermissionsByRoleShortName(@Param("shortName") String shortName);
}

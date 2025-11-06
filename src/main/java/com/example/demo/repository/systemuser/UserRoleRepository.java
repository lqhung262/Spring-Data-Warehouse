package com.example.demo.repository.systemuser;

import com.example.demo.entity.systemuser.User;
import com.example.demo.entity.systemuser.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    List<UserRole> findByUserId(User user);

    @Modifying
    @Query("delete from UserRole ur where ur.userId = :user")
    void deleteByUser(@Param("user") User user);

    @Query("select ur.roleId.shortName from UserRole ur where ur.userId = :user")
    List<String> findRoleShortNamesByUser(@Param("user") User user);
}

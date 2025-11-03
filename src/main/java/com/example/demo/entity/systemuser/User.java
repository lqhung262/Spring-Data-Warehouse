package com.example.demo.entity.systemuser;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "user")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @NotNull
    @Column(name = "authorization_service_user_id", nullable = false, unique = true, length = 100)
    private String authorizationServiceUserId;

    @NotNull
    @Column(name = "user_name", nullable = false, unique = true, length = 100)
    private String userName;

    @NotNull
    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @NotNull
    @Column(name = "email_address", nullable = false, unique = true, length = 150)
    private String emailAddress;

    @NotNull
    @Column(name = "is_enabled")
    private Boolean isEnabled = true;

    @NotNull
    @Column(name = "created_by", nullable = false)
    private Long createdBy = 1L;

    @NotNull
    @Column(name = "updated_by", nullable = false)
    private Long updatedBy = 1L;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;


    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<UserRole> userRoleList;
}

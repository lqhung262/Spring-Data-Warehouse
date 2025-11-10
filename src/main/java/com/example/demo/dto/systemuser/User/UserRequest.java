package com.example.demo.dto.systemuser.User;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {
    @NotBlank
    private String userName;

    @NotBlank
    private String password;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String emailAddress;

    private Set<Long> roleIds;

    // optional flag to set enabled/disabled, needed for POST API and when creating user & Keycloak user
    private Boolean isEnabled;
}

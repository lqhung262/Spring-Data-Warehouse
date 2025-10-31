package com.example.demo.dto.general.UserProfile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileRequest {
    @NotNull
    private Long employeeId;

    @NotBlank
    private String email;

    @NotBlank
    private String fullName;

}

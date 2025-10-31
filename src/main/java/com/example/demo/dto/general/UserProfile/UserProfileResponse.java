package com.example.demo.dto.general.UserProfile;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileResponse {
    Long userProfileId;
    Long employeeId;
    String email;
    String fullName;
}

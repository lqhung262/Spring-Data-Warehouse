package com.example.demo.dto.systemuser.Permission;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionResponse {
    Long permissionId;
    String shortName;
    String description;
    String url;
    String method;
    Boolean isPublic;
}


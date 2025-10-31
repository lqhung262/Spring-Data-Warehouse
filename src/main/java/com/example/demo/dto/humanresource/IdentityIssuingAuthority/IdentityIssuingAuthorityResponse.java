package com.example.demo.dto.humanresource.IdentityIssuingAuthority;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IdentityIssuingAuthorityResponse {
    Long identityIssuingAuthorityId;
    String sourceId;
    Long documentTypeId;
    String name;
}

package com.example.demo.dto.humanresource.IdentityIssuingAuthority;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdentityIssuingAuthorityRequest {
    private String sourceId;
    private Long documentTypeId;
    private String name;
}

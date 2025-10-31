package com.example.demo.mapper.humanresource;

import com.example.demo.dto.humanresource.IdentityIssuingAuthority.IdentityIssuingAuthorityRequest;
import com.example.demo.dto.humanresource.IdentityIssuingAuthority.IdentityIssuingAuthorityResponse;
import com.example.demo.entity.humanresource.IdentityIssuingAuthority;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface IdentityIssuingAuthorityMapper {
    IdentityIssuingAuthority toIdentityIssuingAuthority(IdentityIssuingAuthorityRequest request);

    IdentityIssuingAuthorityResponse toIdentityIssuingAuthorityResponse(IdentityIssuingAuthority identityIA);

    void updateIdentityIssuingAuthority(@MappingTarget IdentityIssuingAuthority identityIA, IdentityIssuingAuthorityRequest request);
}
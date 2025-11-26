package com.example.demo.mapper.humanresource;

import com.example.demo.dto.humanresource.IdentityIssuingAuthority.IdentityIssuingAuthorityRequest;
import com.example.demo.dto.humanresource.IdentityIssuingAuthority.IdentityIssuingAuthorityResponse;
import com.example.demo.entity.humanresource.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface IdentityIssuingAuthorityMapper {

    @Mapping(target = "identityIssuingAuthorityId", ignore = true)
    @Mapping(target = "documentType", ignore = true)
    @Mapping(target = "sourceSystemId", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    IdentityIssuingAuthority toIdentityIssuingAuthority(IdentityIssuingAuthorityRequest request);

    @Mapping(target = "documentTypeId", source = "documentType.documentTypeId")
    IdentityIssuingAuthorityResponse toIdentityIssuingAuthorityResponse(IdentityIssuingAuthority identityIA);

    @Mapping(target = "identityIssuingAuthorityId", ignore = true)
    @Mapping(target = "documentType", ignore = true)
    @Mapping(target = "sourceSystemId", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    void updateIdentityIssuingAuthority(@MappingTarget IdentityIssuingAuthority identityIA, IdentityIssuingAuthorityRequest request);

    default void setReferences(IdentityIssuingAuthority identityIA, IdentityIssuingAuthorityRequest request) {
        if (request.getDocumentTypeId() != null) {
            DocumentType documentType = new DocumentType();
            documentType.setDocumentTypeId(request.getDocumentTypeId());
            identityIA.setDocumentType(documentType);
        }
    }
}
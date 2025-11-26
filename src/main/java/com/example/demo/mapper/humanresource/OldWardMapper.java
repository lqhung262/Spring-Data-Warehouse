package com.example.demo.mapper.humanresource;

import com.example.demo.dto.humanresource.OldWard.OldWardRequest;
import com.example.demo.dto.humanresource.OldWard.OldWardResponse;
import com.example.demo.entity.humanresource.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OldWardMapper {

    @Mapping(target = "oldWardId", ignore = true)
    @Mapping(target = "ward", ignore = true)
    @Mapping(target = "oldDistrict", ignore = true)
    @Mapping(target = "sourceSystemId", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    OldWard toOldWard(OldWardRequest request);

    @Mapping(target = "wardId", source = "ward.wardId")
    @Mapping(target = "oldDistrictId", source = "oldDistrict.oldDistrictId")
    OldWardResponse toOldWardResponse(OldWard oldWard);

    @Mapping(target = "oldWardId", ignore = true)
    @Mapping(target = "ward", ignore = true)
    @Mapping(target = "oldDistrict", ignore = true)
    @Mapping(target = "sourceSystemId", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    void updateOldWard(@MappingTarget OldWard oldWard, OldWardRequest request);

    default void setReferences(OldWard oldWard, OldWardRequest request) {
        if (request.getWardId() != null) {
            Ward ward = new Ward();
            ward.setWardId(request.getWardId());
            oldWard.setWard(ward);
        }
        if (request.getOldDistrictId() != null) {
            OldDistrict oldDistrict = new OldDistrict();
            oldDistrict.setOldDistrictId(request.getOldDistrictId());
            oldWard.setOldDistrict(oldDistrict);
        }
    }
}
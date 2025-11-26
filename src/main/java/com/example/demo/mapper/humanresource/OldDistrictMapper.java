package com.example.demo.mapper.humanresource;

import com.example.demo.dto.humanresource.OldDistrict.OldDistrictRequest;
import com.example.demo.dto.humanresource.OldDistrict.OldDistrictResponse;
import com.example.demo.entity.humanresource.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OldDistrictMapper {

    @Mapping(target = "oldDistrictId", ignore = true)
    @Mapping(target = "ward", ignore = true)
    @Mapping(target = "oldProvinceCity", ignore = true)
    @Mapping(target = "sourceSystemId", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    OldDistrict toOldDistrict(OldDistrictRequest request);

    @Mapping(target = "wardId", source = "ward.wardId")
    @Mapping(target = "oldProvinceCityId", source = "oldProvinceCity.oldProvinceCityId")
    OldDistrictResponse toOldDistrictResponse(OldDistrict oldDistrict);

    @Mapping(target = "oldDistrictId", ignore = true)
    @Mapping(target = "ward", ignore = true)
    @Mapping(target = "oldProvinceCity", ignore = true)
    @Mapping(target = "sourceSystemId", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    void updateOldDistrict(@MappingTarget OldDistrict oldDistrict, OldDistrictRequest request);

    default void setReferences(OldDistrict oldDistrict, OldDistrictRequest request) {
        if (request.getWardId() != null) {
            Ward ward = new Ward();
            ward.setWardId(request.getWardId());
            oldDistrict.setWard(ward);
        }
        if (request.getOldProvinceCityId() != null) {
            OldProvinceCity oldProvinceCity = new OldProvinceCity();
            oldProvinceCity.setOldProvinceCityId(request.getOldProvinceCityId());
            oldDistrict.setOldProvinceCity(oldProvinceCity);
        }
    }
}
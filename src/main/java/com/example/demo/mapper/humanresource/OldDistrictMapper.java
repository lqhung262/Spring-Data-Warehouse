package com.example.demo.mapper.humanresource;

import com.example.demo.dto.humanresource.OldDistrict.OldDistrictRequest;
import com.example.demo.dto.humanresource.OldDistrict.OldDistrictResponse;
import com.example.demo.entity.humanresource.OldDistrict;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OldDistrictMapper {
    OldDistrict toOldDistrict(OldDistrictRequest request);

    OldDistrictResponse toOldDistrictResponse(OldDistrict oldDistrict);

    void updateOldDistrict(@MappingTarget OldDistrict oldDistrict, OldDistrictRequest request);
}
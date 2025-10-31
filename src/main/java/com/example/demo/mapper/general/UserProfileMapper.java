package com.example.demo.mapper.general;

import com.example.demo.dto.general.UserProfile.UserProfileRequest;
import com.example.demo.dto.general.UserProfile.UserProfileResponse;
import com.example.demo.entity.general.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserProfileMapper {
    UserProfile toUserProfile(UserProfileRequest request);

    UserProfileResponse toUserProfileResponse(UserProfile userProfile);

    void updateUserProfile(@MappingTarget UserProfile userProfile, UserProfileRequest request);
}

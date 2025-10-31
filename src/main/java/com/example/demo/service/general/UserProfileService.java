package com.example.demo.service.general;

import com.example.demo.dto.general.UserProfile.UserProfileRequest;
import com.example.demo.dto.general.UserProfile.UserProfileResponse;
import com.example.demo.entity.general.UserProfile;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.general.UserProfileMapper;
import com.example.demo.repository.general.UserProfileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileService {
    final UserProfileRepository userProfileRepository;
    final UserProfileMapper userProfileMapper;

    @Value("${entities.general.userprofile}")
    private String entityName;

    public UserProfileResponse createUserProfile(UserProfileRequest request) {
        UserProfile userProfile = userProfileMapper.toUserProfile(request);

        return userProfileMapper.toUserProfileResponse(userProfileRepository.save(userProfile));
    }

    public List<UserProfileResponse> getUserProfiles(Pageable pageable) {
        Page<UserProfile> page = userProfileRepository.findAll(pageable);
        List<UserProfileResponse> dtos = page.getContent()
                .stream().map(userProfileMapper::toUserProfileResponse).toList();
        return dtos;
    }

    public UserProfileResponse getUserProfile(Long id) {
        return userProfileMapper.toUserProfileResponse(userProfileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public UserProfileResponse updateUserProfile(Long id, UserProfileRequest request) {
        UserProfile userProfile = userProfileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        userProfileMapper.updateUserProfile(userProfile, request);

        return userProfileMapper.toUserProfileResponse(userProfileRepository.save(userProfile));
    }

    public void deleteUserProfile(Long id) {
        UserProfile userProfile = userProfileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        userProfileRepository.deleteById(id);
    }
}

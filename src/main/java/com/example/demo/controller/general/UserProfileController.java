package com.example.demo.controller.general;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.general.UserProfile.UserProfileRequest;
import com.example.demo.dto.general.UserProfile.UserProfileResponse;
import com.example.demo.service.general.UserProfileService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-profiles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserProfileController {
    UserProfileService userProfileService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<UserProfileResponse> createUserProfile(@Valid @RequestBody UserProfileRequest request) {
        ApiResponse<UserProfileResponse> response = new ApiResponse<>();

        response.setResult(userProfileService.createUserProfile(request));

        return response;
    }

    @GetMapping()
    ApiResponse<List<UserProfileResponse>> getUserProfiles(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                           @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                           @RequestParam(required = false, defaultValue = "fullName") String sortBy,
                                                           @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<UserProfileResponse>>builder()
                .result(userProfileService.getUserProfiles(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{userProfileId}")
    ApiResponse<UserProfileResponse> getUserProfile(@PathVariable("userProfileId") Long userProfileId) {
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.getUserProfile(userProfileId))
                .build();
    }

    @PutMapping("/{userProfileId}")
    ApiResponse<UserProfileResponse> updateUserProfile(@PathVariable("userProfileId") Long userProfileId, @RequestBody UserProfileRequest request) {
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.updateUserProfile(userProfileId, request))
                .build();
    }

    @DeleteMapping("/{userProfileId}")
    ApiResponse<String> deleteUserProfile(@PathVariable Long userProfileId) {
        userProfileService.deleteUserProfile(userProfileId);
        return ApiResponse.<String>builder().result("UserProfile has been deleted").build();
    }
}

package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.BloodGroup.BloodGroupRequest;
import com.example.demo.dto.humanresource.BloodGroup.BloodGroupResponse;
import com.example.demo.service.humanresource.BloodGroupService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bloodGroupIds")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BloodGroupController {
    BloodGroupService bloodGroupIdService;

    @PostMapping()
    ApiResponse<BloodGroupResponse> createBloodGroup(@Valid @RequestBody BloodGroupRequest request) {
        ApiResponse<BloodGroupResponse> response = new ApiResponse<>();

        response.setResult(bloodGroupIdService.createBloodGroup(request));

        return response;
    }

    @GetMapping()
    ApiResponse<List<BloodGroupResponse>> getBloodGroups(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                         @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                         @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                         @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<BloodGroupResponse>>builder()
                .result(bloodGroupIdService.getBloodGroups(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{bloodGroupIdId}")
    ApiResponse<BloodGroupResponse> getBloodGroup(@PathVariable("bloodGroupIdId") Long bloodGroupIdId) {
        return ApiResponse.<BloodGroupResponse>builder()
                .result(bloodGroupIdService.getBloodGroup(bloodGroupIdId))
                .build();
    }

    @PutMapping("/{bloodGroupIdId}")
    ApiResponse<BloodGroupResponse> updateBloodGroup(@PathVariable("bloodGroupIdId") Long bloodGroupIdId, @RequestBody BloodGroupRequest request) {
        return ApiResponse.<BloodGroupResponse>builder()
                .result(bloodGroupIdService.updateBloodGroup(bloodGroupIdId, request))
                .build();
    }

    @DeleteMapping("/{bloodGroupIdId}")
    ApiResponse<String> deleteBloodGroup(@PathVariable Long bloodGroupIdId) {
        bloodGroupIdService.deleteBloodGroup(bloodGroupIdId);
        return ApiResponse.<String>builder().result("Blood Group has been deleted").build();
    }
}

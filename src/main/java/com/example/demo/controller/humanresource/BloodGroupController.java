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
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/blood-groups")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BloodGroupController {
    BloodGroupService bloodGroupService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<BloodGroupResponse> createBloodGroup(@Valid @RequestBody BloodGroupRequest request) {
        ApiResponse<BloodGroupResponse> response = new ApiResponse<>();

        response.setResult(bloodGroupService.createBloodGroup(request));

        return response;
    }

    @PostMapping("/_bulk-upsert")
    ApiResponse<List<BloodGroupResponse>> bulkBloodGroupUpsert(@Valid @RequestBody List<BloodGroupRequest> requests) {
        return ApiResponse.<List<BloodGroupResponse>>builder()
                .result(bloodGroupService.bulkUpsertBloodGroups(requests))
                .build();
    }

    @DeleteMapping("/_bulk-delete")
    public ApiResponse<String> bulkDeleteBloodGroups(@Valid @RequestParam("ids") List<Long> bloodGroupIds) {
        bloodGroupService.bulkDeleteBloodGroups(bloodGroupIds);
        return ApiResponse.<String>builder()
                .result(bloodGroupIds.size() + " bloodGroups have been deleted.")
                .build();
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
                .result(bloodGroupService.getBloodGroups(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{bloodGroupId}")
    ApiResponse<BloodGroupResponse> getBloodGroup(@PathVariable("bloodGroupId") Long bloodGroupId) {
        return ApiResponse.<BloodGroupResponse>builder()
                .result(bloodGroupService.getBloodGroup(bloodGroupId))
                .build();
    }

    @PutMapping("/{bloodGroupId}")
    ApiResponse<BloodGroupResponse> updateBloodGroup(@PathVariable("bloodGroupId") Long bloodGroupId, @RequestBody BloodGroupRequest request) {
        return ApiResponse.<BloodGroupResponse>builder()
                .result(bloodGroupService.updateBloodGroup(bloodGroupId, request))
                .build();
    }

    @DeleteMapping("/{bloodGroupId}")
    ApiResponse<String> deleteBloodGroup(@PathVariable Long bloodGroupId) {
        bloodGroupService.deleteBloodGroup(bloodGroupId);
        return ApiResponse.<String>builder().result("Blood Group has been deleted").build();
    }
}

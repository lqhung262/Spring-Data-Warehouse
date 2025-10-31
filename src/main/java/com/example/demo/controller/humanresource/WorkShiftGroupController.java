package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.WorkShiftGroup.WorkShiftGroupRequest;
import com.example.demo.dto.humanresource.WorkShiftGroup.WorkShiftGroupResponse;
import com.example.demo.service.humanresource.WorkShiftGroupService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workShiftGroups")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WorkShiftGroupController {
    WorkShiftGroupService workShiftGroupService;

    @PostMapping()
    ApiResponse<WorkShiftGroupResponse> createWorkShiftGroup(@Valid @RequestBody WorkShiftGroupRequest request) {
        ApiResponse<WorkShiftGroupResponse> response = new ApiResponse<>();

        response.setResult(workShiftGroupService.createWorkShiftGroup(request));

        return response;
    }

    @GetMapping()
    ApiResponse<List<WorkShiftGroupResponse>> getWorkShiftGroups(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                                 @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                                 @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                                 @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<WorkShiftGroupResponse>>builder()
                .result(workShiftGroupService.getWorkShiftGroups(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{workShiftGroupId}")
    ApiResponse<WorkShiftGroupResponse> getWorkShiftGroup(@PathVariable("workShiftGroupId") Long workShiftGroupId) {
        return ApiResponse.<WorkShiftGroupResponse>builder()
                .result(workShiftGroupService.getWorkShiftGroup(workShiftGroupId))
                .build();
    }

    @PutMapping("/{workShiftGroupId}")
    ApiResponse<WorkShiftGroupResponse> updateWorkShiftGroup(@PathVariable("workShiftGroupId") Long workShiftGroupId, @RequestBody WorkShiftGroupRequest request) {
        return ApiResponse.<WorkShiftGroupResponse>builder()
                .result(workShiftGroupService.updateWorkShiftGroup(workShiftGroupId, request))
                .build();
    }

    @DeleteMapping("/{workShiftGroupId}")
    ApiResponse<String> deleteWorkShiftGroup(@PathVariable Long workShiftGroupId) {
        workShiftGroupService.deleteWorkShiftGroup(workShiftGroupId);
        return ApiResponse.<String>builder().result("Work Shift Group has been deleted").build();
    }
}

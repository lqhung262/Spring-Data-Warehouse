package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.humanresource.WorkShiftGroup.WorkShiftGroupRequest;
import com.example.demo.dto.humanresource.WorkShiftGroup.WorkShiftGroupResponse;
import com.example.demo.service.humanresource.WorkShiftGroupService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.controller.humanresource.AttendanceMachineController.getBulkOperationResultApiResponse;

@RestController
@RequestMapping("/work-shift-groups")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WorkShiftGroupController {
    WorkShiftGroupService workShiftGroupService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<WorkShiftGroupResponse> createWorkShiftGroup(@Valid @RequestBody WorkShiftGroupRequest request) {
        ApiResponse<WorkShiftGroupResponse> response = new ApiResponse<>();

        response.setResult(workShiftGroupService.createWorkShiftGroup(request));

        return response;
    }

    /**
     * BULK UPSERT ENDPOINT
     */
    @PostMapping("/_bulk-upsert")
    @ResponseStatus(HttpStatus.OK)
    ApiResponse<BulkOperationResult<WorkShiftGroupResponse>> bulkUpsertWorkShiftGroups(
            @Valid @RequestBody List<WorkShiftGroupRequest> requests) {

        BulkOperationResult<WorkShiftGroupResponse> result =
                workShiftGroupService.bulkUpsertWorkShiftGroups(requests);

        // Determine response code based on result
        int responseCode;
        if (!result.hasErrors()) {
            // Trường hợp 1: Không có lỗi nào -> Thành công toàn bộ
            responseCode = 1000;
        } else if (result.hasSuccess()) {
            // Trường hợp 2: Có lỗi NHƯNG cũng có thành công -> Thành công một phần (Multi-Status)
            responseCode = 207;
        } else {
            // Trường hợp 3: Có lỗi VÀ không có thành công nào -> Thất bại toàn bộ
            responseCode = 400;
        }

        return ApiResponse.<BulkOperationResult<WorkShiftGroupResponse>>builder()
                .code(responseCode)
                .message(result.getSummary())
                .result(result)
                .build();
    }

    /**
     * BULK DELETE
     */
    @DeleteMapping("/_bulk-delete")
    @ResponseStatus(HttpStatus.OK)
    ApiResponse<BulkOperationResult<Long>> bulkDeleteWorkShiftGroups(@RequestParam("ids") List<Long> ids) {

        BulkOperationResult<Long> result = workShiftGroupService.bulkDeleteWorkShiftGroups(ids);

        // Determine response code
        return getBulkOperationResultApiResponse(result);
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

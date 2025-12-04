package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.humanresource.School.SchoolRequest;
import com.example.demo.dto.humanresource.School.SchoolResponse;
import com.example.demo.service.humanresource.SchoolService;
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
@RequestMapping("/schools")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SchoolController {
    SchoolService schoolService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<SchoolResponse> createSchool(@Valid @RequestBody SchoolRequest request) {
        ApiResponse<SchoolResponse> response = new ApiResponse<>();

        response.setResult(schoolService.createSchool(request));

        return response;
    }

    /**
     * BULK UPSERT ENDPOINT
     */
    @PostMapping("/_bulk-upsert")
    @ResponseStatus(HttpStatus.OK)
    ApiResponse<BulkOperationResult<SchoolResponse>> bulkUpsertSchools(
            @Valid @RequestBody List<SchoolRequest> requests) {

        BulkOperationResult<SchoolResponse> result =
                schoolService.bulkUpsertSchools(requests);

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

        return ApiResponse.<BulkOperationResult<SchoolResponse>>builder()
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
    ApiResponse<BulkOperationResult<Long>> bulkDeleteSchools(@RequestParam("ids") List<Long> ids) {

        BulkOperationResult<Long> result = schoolService.bulkDeleteSchools(ids);

        // Determine response code
        return getBulkOperationResultApiResponse(result);
    }

    @GetMapping()
    ApiResponse<List<SchoolResponse>> getSchools(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                 @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                 @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                 @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<SchoolResponse>>builder()
                .result(schoolService.getSchools(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{schoolId}")
    ApiResponse<SchoolResponse> getSchool(@PathVariable("schoolId") Long schoolId) {
        return ApiResponse.<SchoolResponse>builder()
                .result(schoolService.getSchool(schoolId))
                .build();
    }

    @PutMapping("/{schoolId}")
    ApiResponse<SchoolResponse> updateSchool(@PathVariable("schoolId") Long schoolId, @RequestBody SchoolRequest request) {
        return ApiResponse.<SchoolResponse>builder()
                .result(schoolService.updateSchool(schoolId, request))
                .build();
    }

    @DeleteMapping("/{schoolId}")
    ApiResponse<String> deleteSchool(@PathVariable Long schoolId) {
        schoolService.deleteSchool(schoolId);
        return ApiResponse.<String>builder().result("School has been deleted").build();
    }
}

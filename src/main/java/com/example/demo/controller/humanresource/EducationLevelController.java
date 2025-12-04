package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.humanresource.EducationLevel.EducationLevelRequest;
import com.example.demo.dto.humanresource.EducationLevel.EducationLevelResponse;
import com.example.demo.service.humanresource.EducationLevelService;
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
@RequestMapping("/education-levels")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EducationLevelController {
    EducationLevelService educationLevelService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<EducationLevelResponse> createEducationLevel(@Valid @RequestBody EducationLevelRequest request) {
        ApiResponse<EducationLevelResponse> response = new ApiResponse<>();

        response.setResult(educationLevelService.createEducationLevel(request));

        return response;
    }

    /**
     * BULK UPSERT ENDPOINT
     */
    @PostMapping("/_bulk-upsert")
    @ResponseStatus(HttpStatus.OK)
    ApiResponse<BulkOperationResult<EducationLevelResponse>> bulkUpsertEducationLevels(
            @Valid @RequestBody List<EducationLevelRequest> requests) {

        BulkOperationResult<EducationLevelResponse> result =
                educationLevelService.bulkUpsertEducationLevels(requests);

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

        return ApiResponse.<BulkOperationResult<EducationLevelResponse>>builder()
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
    ApiResponse<BulkOperationResult<Long>> bulkDeleteEducationLevels(@RequestParam("ids") List<Long> ids) {

        BulkOperationResult<Long> result = educationLevelService.bulkDeleteEducationLevels(ids);

        // Determine response code
        return getBulkOperationResultApiResponse(result);
    }

    @GetMapping()
    ApiResponse<List<EducationLevelResponse>> getEducationLevels(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                                 @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                                 @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                                 @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<EducationLevelResponse>>builder()
                .result(educationLevelService.getEducationLevels(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{educationLevelId}")
    ApiResponse<EducationLevelResponse> getEducationLevel(@PathVariable("educationLevelId") Long educationLevelId) {
        return ApiResponse.<EducationLevelResponse>builder()
                .result(educationLevelService.getEducationLevel(educationLevelId))
                .build();
    }

    @PutMapping("/{educationLevelId}")
    ApiResponse<EducationLevelResponse> updateEducationLevel(@PathVariable("educationLevelId") Long educationLevelId, @RequestBody EducationLevelRequest request) {
        return ApiResponse.<EducationLevelResponse>builder()
                .result(educationLevelService.updateEducationLevel(educationLevelId, request))
                .build();
    }

    @DeleteMapping("/{educationLevelId}")
    ApiResponse<String> deleteEducationLevel(@PathVariable Long educationLevelId) {
        educationLevelService.deleteEducationLevel(educationLevelId);
        return ApiResponse.<String>builder().result("Education Level has been deleted").build();
    }
}

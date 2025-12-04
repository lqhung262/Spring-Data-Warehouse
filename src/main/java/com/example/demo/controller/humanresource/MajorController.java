package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.humanresource.Major.MajorRequest;
import com.example.demo.dto.humanresource.Major.MajorResponse;
import com.example.demo.service.humanresource.MajorService;
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
@RequestMapping("/majors")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MajorController {
    MajorService majorService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<MajorResponse> createMajor(@Valid @RequestBody MajorRequest request) {
        ApiResponse<MajorResponse> response = new ApiResponse<>();

        response.setResult(majorService.createMajor(request));

        return response;
    }

    /**
     * BULK UPSERT ENDPOINT
     */
    @PostMapping("/_bulk-upsert")
    @ResponseStatus(HttpStatus.OK)
    ApiResponse<BulkOperationResult<MajorResponse>> bulkUpsertMajors(
            @Valid @RequestBody List<MajorRequest> requests) {

        BulkOperationResult<MajorResponse> result =
                majorService.bulkUpsertMajors(requests);

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

        return ApiResponse.<BulkOperationResult<MajorResponse>>builder()
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
    ApiResponse<BulkOperationResult<Long>> bulkDeleteMajors(@RequestParam("ids") List<Long> ids) {

        BulkOperationResult<Long> result = majorService.bulkDeleteMajors(ids);

        // Determine response code
        return getBulkOperationResultApiResponse(result);
    }

    @GetMapping()
    ApiResponse<List<MajorResponse>> getMajors(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                               @RequestParam(required = false, defaultValue = "5") int pageSize,
                                               @RequestParam(required = false, defaultValue = "name") String sortBy,
                                               @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<MajorResponse>>builder()
                .result(majorService.getMajors(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{majorId}")
    ApiResponse<MajorResponse> getMajor(@PathVariable("majorId") Long majorId) {
        return ApiResponse.<MajorResponse>builder()
                .result(majorService.getMajor(majorId))
                .build();
    }

    @PutMapping("/{majorId}")
    ApiResponse<MajorResponse> updateMajor(@PathVariable("majorId") Long majorId, @RequestBody MajorRequest request) {
        return ApiResponse.<MajorResponse>builder()
                .result(majorService.updateMajor(majorId, request))
                .build();
    }

    @DeleteMapping("/{majorId}")
    ApiResponse<String> deleteMajor(@PathVariable Long majorId) {
        majorService.deleteMajor(majorId);
        return ApiResponse.<String>builder().result("Major has been deleted").build();
    }
}

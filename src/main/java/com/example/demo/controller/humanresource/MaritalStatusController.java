package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.humanresource.MaritalStatus.MaritalStatusRequest;
import com.example.demo.dto.humanresource.MaritalStatus.MaritalStatusResponse;
import com.example.demo.service.humanresource.MaritalStatusService;
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
@RequestMapping("/marital-statuses")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MaritalStatusController {
    MaritalStatusService maritalStatusService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<MaritalStatusResponse> createMaritalStatus(@Valid @RequestBody MaritalStatusRequest request) {
        ApiResponse<MaritalStatusResponse> response = new ApiResponse<>();

        response.setResult(maritalStatusService.createMaritalStatus(request));

        return response;
    }

    /**
     * BULK UPSERT ENDPOINT
     */
    @PostMapping("/_bulk-upsert")
    @ResponseStatus(HttpStatus.OK)
    ApiResponse<BulkOperationResult<MaritalStatusResponse>> bulkUpsertMaritalStatuses(
            @Valid @RequestBody List<MaritalStatusRequest> requests) {

        BulkOperationResult<MaritalStatusResponse> result =
                maritalStatusService.bulkUpsertMaritalStatuses(requests);

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

        return ApiResponse.<BulkOperationResult<MaritalStatusResponse>>builder()
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
    ApiResponse<BulkOperationResult<Long>> bulkDeleteMaritalStatuses(@RequestParam("ids") List<Long> ids) {

        BulkOperationResult<Long> result = maritalStatusService.bulkDeleteMaritalStatuses(ids);

        // Determine response code
        return getBulkOperationResultApiResponse(result);
    }

    @GetMapping()
    ApiResponse<List<MaritalStatusResponse>> getMaritalStatuss(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                               @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                               @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                               @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<MaritalStatusResponse>>builder()
                .result(maritalStatusService.getMaritalStatuses(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{maritalStatusId}")
    ApiResponse<MaritalStatusResponse> getMaritalStatus(@PathVariable("maritalStatusId") Long maritalStatusId) {
        return ApiResponse.<MaritalStatusResponse>builder()
                .result(maritalStatusService.getMaritalStatus(maritalStatusId))
                .build();
    }

    @PutMapping("/{maritalStatusId}")
    ApiResponse<MaritalStatusResponse> updateMaritalStatus(@PathVariable("maritalStatusId") Long maritalStatusId, @RequestBody MaritalStatusRequest request) {
        return ApiResponse.<MaritalStatusResponse>builder()
                .result(maritalStatusService.updateMaritalStatus(maritalStatusId, request))
                .build();
    }

    @DeleteMapping("/{maritalStatusId}")
    ApiResponse<String> deleteMaritalStatus(@PathVariable Long maritalStatusId) {
        maritalStatusService.deleteMaritalStatus(maritalStatusId);
        return ApiResponse.<String>builder().result("Marital Status has been deleted").build();
    }
}

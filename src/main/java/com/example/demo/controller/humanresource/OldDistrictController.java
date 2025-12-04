package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.humanresource.OldDistrict.OldDistrictRequest;
import com.example.demo.dto.humanresource.OldDistrict.OldDistrictResponse;
import com.example.demo.service.humanresource.OldDistrictService;
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
@RequestMapping("/old-districts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OldDistrictController {
    OldDistrictService oldDistrictService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<OldDistrictResponse> createOldDistrict(@Valid @RequestBody OldDistrictRequest request) {
        ApiResponse<OldDistrictResponse> response = new ApiResponse<>();

        response.setResult(oldDistrictService.createOldDistrict(request));

        return response;
    }

    /**
     * BULK UPSERT ENDPOINT
     */
    @PostMapping("/_bulk-upsert")
    @ResponseStatus(HttpStatus.OK)
    ApiResponse<BulkOperationResult<OldDistrictResponse>> bulkUpsertOldDistricts(
            @Valid @RequestBody List<OldDistrictRequest> requests) {

        BulkOperationResult<OldDistrictResponse> result =
                oldDistrictService.bulkUpsertOldDistricts(requests);

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

        return ApiResponse.<BulkOperationResult<OldDistrictResponse>>builder()
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
    ApiResponse<BulkOperationResult<Long>> bulkDeleteOldDistricts(@RequestParam("ids") List<Long> ids) {

        BulkOperationResult<Long> result = oldDistrictService.bulkDeleteOldDistricts(ids);

        // Determine response code
        return getBulkOperationResultApiResponse(result);
    }

    @GetMapping()
    ApiResponse<List<OldDistrictResponse>> getOldDistricts(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                           @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                           @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                           @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        return ApiResponse.<List<OldDistrictResponse>>builder()
                .result(oldDistrictService.getOldDistricts(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{oldDistrictId}")
    ApiResponse<OldDistrictResponse> getOldDistrict(@PathVariable("oldDistrictId") Long oldDistrictId) {
        return ApiResponse.<OldDistrictResponse>builder()
                .result(oldDistrictService.getOldDistrict(oldDistrictId))
                .build();
    }

    @PutMapping("/{oldDistrictId}")
    ApiResponse<OldDistrictResponse> updateOldDistrict(@PathVariable("oldDistrictId") Long oldDistrictId, @RequestBody OldDistrictRequest request) {
        return ApiResponse.<OldDistrictResponse>builder()
                .result(oldDistrictService.updateOldDistrict(oldDistrictId, request))
                .build();
    }

    @DeleteMapping("/{oldDistrictId}")
    ApiResponse<String> deleteOldDistrict(@PathVariable Long oldDistrictId) {
        oldDistrictService.deleteOldDistrict(oldDistrictId);
        return ApiResponse.<String>builder().result("Old District has been deleted").build();
    }
}

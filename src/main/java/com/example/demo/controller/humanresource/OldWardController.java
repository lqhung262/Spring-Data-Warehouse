package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.humanresource.OldWard.OldWardRequest;
import com.example.demo.dto.humanresource.OldWard.OldWardResponse;
import com.example.demo.service.humanresource.OldWardService;
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
@RequestMapping("/old-wards")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OldWardController {
    OldWardService oldWardService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<OldWardResponse> createOldWard(@Valid @RequestBody OldWardRequest request) {
        ApiResponse<OldWardResponse> response = new ApiResponse<>();

        response.setResult(oldWardService.createOldWard(request));

        return response;
    }

    /**
     * BULK UPSERT ENDPOINT
     */
    @PostMapping("/_bulk-upsert")
    @ResponseStatus(HttpStatus.OK)
    ApiResponse<BulkOperationResult<OldWardResponse>> bulkUpsertOldWards(
            @Valid @RequestBody List<OldWardRequest> requests) {

        BulkOperationResult<OldWardResponse> result =
                oldWardService.bulkUpsertOldWards(requests);

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

        return ApiResponse.<BulkOperationResult<OldWardResponse>>builder()
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
    ApiResponse<BulkOperationResult<Long>> bulkDeleteOldWards(@RequestParam("ids") List<Long> ids) {

        BulkOperationResult<Long> result = oldWardService.bulkDeleteOldWards(ids);

        // Determine response code
        return getBulkOperationResultApiResponse(result);
    }

    @GetMapping()
    ApiResponse<List<OldWardResponse>> getOldWards(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                   @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                   @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                   @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<OldWardResponse>>builder()
                .result(oldWardService.getOldWards(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{oldWardId}")
    ApiResponse<OldWardResponse> getOldWard(@PathVariable("oldWardId") Long oldWardId) {
        return ApiResponse.<OldWardResponse>builder()
                .result(oldWardService.getOldWard(oldWardId))
                .build();
    }

    @PutMapping("/{oldWardId}")
    ApiResponse<OldWardResponse> updateOldWard(@PathVariable("oldWardId") Long oldWardId, @RequestBody OldWardRequest request) {
        return ApiResponse.<OldWardResponse>builder()
                .result(oldWardService.updateOldWard(oldWardId, request))
                .build();
    }

    @DeleteMapping("/{oldWardId}")
    ApiResponse<String> deleteOldWard(@PathVariable Long oldWardId) {
        oldWardService.deleteOldWard(oldWardId);
        return ApiResponse.<String>builder().result("Old Ward has been deleted").build();
    }
}

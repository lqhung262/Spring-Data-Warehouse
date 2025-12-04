package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.humanresource.Ward.WardRequest;
import com.example.demo.dto.humanresource.Ward.WardResponse;
import com.example.demo.service.humanresource.WardService;
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
@RequestMapping("/wards")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WardController {
    WardService wardService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<WardResponse> createWard(@Valid @RequestBody WardRequest request) {
        ApiResponse<WardResponse> response = new ApiResponse<>();

        response.setResult(wardService.createWard(request));

        return response;
    }

    /**
     * BULK UPSERT ENDPOINT
     */
    @PostMapping("/_bulk-upsert")
    @ResponseStatus(HttpStatus.OK)
    ApiResponse<BulkOperationResult<WardResponse>> bulkUpsertWards(
            @Valid @RequestBody List<WardRequest> requests) {

        BulkOperationResult<WardResponse> result =
                wardService.bulkUpsertWards(requests);

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

        return ApiResponse.<BulkOperationResult<WardResponse>>builder()
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
    ApiResponse<BulkOperationResult<Long>> bulkDeleteWards(@RequestParam("ids") List<Long> ids) {

        BulkOperationResult<Long> result = wardService.bulkDeleteWards(ids);

        // Determine response code
        return getBulkOperationResultApiResponse(result);
    }

    @GetMapping()
    ApiResponse<List<WardResponse>> getWards(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                             @RequestParam(required = false, defaultValue = "5") int pageSize,
                                             @RequestParam(required = false, defaultValue = "name") String sortBy,
                                             @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<WardResponse>>builder()
                .result(wardService.getWards(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{wardId}")
    ApiResponse<WardResponse> getWard(@PathVariable("wardId") Long wardId) {
        return ApiResponse.<WardResponse>builder()
                .result(wardService.getWard(wardId))
                .build();
    }

    @PutMapping("/{wardId}")
    ApiResponse<WardResponse> updateWard(@PathVariable("wardId") Long wardId, @RequestBody WardRequest request) {
        return ApiResponse.<WardResponse>builder()
                .result(wardService.updateWard(wardId, request))
                .build();
    }

    @DeleteMapping("/{wardId}")
    ApiResponse<String> deleteWard(@PathVariable Long wardId) {
        wardService.deleteWard(wardId);
        return ApiResponse.<String>builder().result(" Ward has been deleted").build();
    }
}

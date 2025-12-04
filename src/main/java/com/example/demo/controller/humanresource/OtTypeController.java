package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.humanresource.OtType.OtTypeRequest;
import com.example.demo.dto.humanresource.OtType.OtTypeResponse;
import com.example.demo.service.humanresource.OtTypeService;
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
@RequestMapping("/ot-types")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OtTypeController {
    OtTypeService otTypeService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<OtTypeResponse> createOtType(@Valid @RequestBody OtTypeRequest request) {
        ApiResponse<OtTypeResponse> response = new ApiResponse<>();

        response.setResult(otTypeService.createOtType(request));

        return response;
    }

    /**
     * BULK UPSERT ENDPOINT
     */
    @PostMapping("/_bulk-upsert")
    @ResponseStatus(HttpStatus.OK)
    ApiResponse<BulkOperationResult<OtTypeResponse>> bulkUpsertOtTypes(
            @Valid @RequestBody List<OtTypeRequest> requests) {

        BulkOperationResult<OtTypeResponse> result =
                otTypeService.bulkUpsertOtTypes(requests);

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

        return ApiResponse.<BulkOperationResult<OtTypeResponse>>builder()
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
    ApiResponse<BulkOperationResult<Long>> bulkDeleteOtTypes(@RequestParam("ids") List<Long> ids) {

        BulkOperationResult<Long> result = otTypeService.bulkDeleteOtTypes(ids);

        // Determine response code
        return getBulkOperationResultApiResponse(result);
    }

    @GetMapping()
    ApiResponse<List<OtTypeResponse>> getOtTypes(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                 @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                 @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                 @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<OtTypeResponse>>builder()
                .result(otTypeService.getOtTypes(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{otTypeId}")
    ApiResponse<OtTypeResponse> getOtType(@PathVariable("otTypeId") Long otTypeId) {
        return ApiResponse.<OtTypeResponse>builder()
                .result(otTypeService.getOtType(otTypeId))
                .build();
    }

    @PutMapping("/{otTypeId}")
    ApiResponse<OtTypeResponse> updateOtType(@PathVariable("otTypeId") Long otTypeId, @RequestBody OtTypeRequest request) {
        return ApiResponse.<OtTypeResponse>builder()
                .result(otTypeService.updateOtType(otTypeId, request))
                .build();
    }

    @DeleteMapping("/{otTypeId}")
    ApiResponse<String> deleteOtType(@PathVariable Long otTypeId) {
        otTypeService.deleteOtType(otTypeId);
        return ApiResponse.<String>builder().result("Ot Type has been deleted").build();
    }
}

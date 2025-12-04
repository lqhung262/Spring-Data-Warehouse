package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.humanresource.ProvinceCity.ProvinceCityRequest;
import com.example.demo.dto.humanresource.ProvinceCity.ProvinceCityResponse;
import com.example.demo.service.humanresource.ProvinceCityService;
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
@RequestMapping("/province-cities")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProvinceCityController {
    ProvinceCityService provinceCityService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<ProvinceCityResponse> createProvinceCity(@Valid @RequestBody ProvinceCityRequest request) {
        ApiResponse<ProvinceCityResponse> response = new ApiResponse<>();

        response.setResult(provinceCityService.createProvinceCity(request));

        return response;
    }

    /**
     * BULK UPSERT ENDPOINT
     */
    @PostMapping("/_bulk-upsert")
    @ResponseStatus(HttpStatus.OK)
    ApiResponse<BulkOperationResult<ProvinceCityResponse>> bulkUpsertProvinceCities(
            @Valid @RequestBody List<ProvinceCityRequest> requests) {

        BulkOperationResult<ProvinceCityResponse> result =
                provinceCityService.bulkUpsertProvinceCities(requests);

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

        return ApiResponse.<BulkOperationResult<ProvinceCityResponse>>builder()
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
    ApiResponse<BulkOperationResult<Long>> bulkDeleteProvinceCities(@RequestParam("ids") List<Long> ids) {

        BulkOperationResult<Long> result = provinceCityService.bulkDeleteProvinceCities(ids);

        // Determine response code
        return getBulkOperationResultApiResponse(result);
    }

    @GetMapping()
    ApiResponse<List<ProvinceCityResponse>> getProvinceCities(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                              @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                              @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                              @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<ProvinceCityResponse>>builder()
                .result(provinceCityService.getProvinceCities(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{provinceCityId}")
    ApiResponse<ProvinceCityResponse> getProvinceCity(@PathVariable("provinceCityId") Long provinceCityId) {
        return ApiResponse.<ProvinceCityResponse>builder()
                .result(provinceCityService.getProvinceCity(provinceCityId))
                .build();
    }

    @PutMapping("/{provinceCityId}")
    ApiResponse<ProvinceCityResponse> updateProvinceCity(@PathVariable("provinceCityId") Long provinceCityId, @RequestBody ProvinceCityRequest request) {
        return ApiResponse.<ProvinceCityResponse>builder()
                .result(provinceCityService.updateProvinceCity(provinceCityId, request))
                .build();
    }

    @DeleteMapping("/{provinceCityId}")
    ApiResponse<String> deleteProvinceCity(@PathVariable Long provinceCityId) {
        provinceCityService.deleteProvinceCity(provinceCityId);
        return ApiResponse.<String>builder().result(" ProvinceCity has been deleted").build();
    }
}

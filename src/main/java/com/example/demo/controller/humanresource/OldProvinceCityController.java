package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.OldProvinceCity.OldProvinceCityRequest;
import com.example.demo.dto.humanresource.OldProvinceCity.OldProvinceCityResponse;
import com.example.demo.service.humanresource.OldProvinceCityService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/old-province-cities")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OldProvinceCityController {
    OldProvinceCityService oldProvinceCityService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<OldProvinceCityResponse> createOldProvinceCity(@Valid @RequestBody OldProvinceCityRequest request) {
        ApiResponse<OldProvinceCityResponse> response = new ApiResponse<>();

        response.setResult(oldProvinceCityService.createOldProvinceCity(request));

        return response;
    }

    @PostMapping("/_bulk-upsert")
    ApiResponse<List<OldProvinceCityResponse>> bulkOldProvinceCityUpsert(@Valid @RequestBody List<OldProvinceCityRequest> requests) {
        return ApiResponse.<List<OldProvinceCityResponse>>builder()
                .result(oldProvinceCityService.bulkUpsertOldProvinceCities(requests))
                .build();
    }

    @DeleteMapping("/_bulk-delete")
    public ApiResponse<String> bulkDeleteOldProvinceCities(@Valid @RequestParam("ids") List<Long> oldProvinceCityIds) {
        oldProvinceCityService.bulkDeleteOldProvinceCities(oldProvinceCityIds);
        return ApiResponse.<String>builder()
                .result(oldProvinceCityIds.size() + " oldProvinceCities have been deleted.")
                .build();
    }

    @GetMapping()
    ApiResponse<List<OldProvinceCityResponse>> getOldProvinceCities(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                                    @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                                    @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                                    @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<OldProvinceCityResponse>>builder()
                .result(oldProvinceCityService.getOldProvinceCities(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{oldProvinceCityId}")
    ApiResponse<OldProvinceCityResponse> getOldProvinceCity(@PathVariable("oldProvinceCityId") Long oldProvinceCityId) {
        return ApiResponse.<OldProvinceCityResponse>builder()
                .result(oldProvinceCityService.getOldProvinceCity(oldProvinceCityId))
                .build();
    }

    @PutMapping("/{oldProvinceCityId}")
    ApiResponse<OldProvinceCityResponse> updateOldProvinceCity(@PathVariable("oldProvinceCityId") Long oldProvinceCityId, @RequestBody OldProvinceCityRequest request) {
        return ApiResponse.<OldProvinceCityResponse>builder()
                .result(oldProvinceCityService.updateOldProvinceCity(oldProvinceCityId, request))
                .build();
    }

    @DeleteMapping("/{oldProvinceCityId}")
    ApiResponse<String> deleteOldProvinceCity(@PathVariable Long oldProvinceCityId) {
        oldProvinceCityService.deleteOldProvinceCity(oldProvinceCityId);
        return ApiResponse.<String>builder().result("Old Province City has been deleted").build();
    }
}

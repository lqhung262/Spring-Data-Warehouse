package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.ProvinceCity.ProvinceCityRequest;
import com.example.demo.dto.humanresource.ProvinceCity.ProvinceCityResponse;
import com.example.demo.service.humanresource.ProvinceCityService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ProvinceCities")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProvinceCityController {
    ProvinceCityService ProvinceCityService;

    @PostMapping()
    ApiResponse<ProvinceCityResponse> createProvinceCity(@Valid @RequestBody ProvinceCityRequest request) {
        ApiResponse<ProvinceCityResponse> response = new ApiResponse<>();

        response.setResult(ProvinceCityService.createProvinceCity(request));

        return response;
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
                .result(ProvinceCityService.getProvinceCities(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{ProvinceCityId}")
    ApiResponse<ProvinceCityResponse> getProvinceCity(@PathVariable("ProvinceCityId") Long ProvinceCityId) {
        return ApiResponse.<ProvinceCityResponse>builder()
                .result(ProvinceCityService.getProvinceCity(ProvinceCityId))
                .build();
    }

    @PutMapping("/{ProvinceCityId}")
    ApiResponse<ProvinceCityResponse> updateProvinceCity(@PathVariable("ProvinceCityId") Long ProvinceCityId, @RequestBody ProvinceCityRequest request) {
        return ApiResponse.<ProvinceCityResponse>builder()
                .result(ProvinceCityService.updateProvinceCity(ProvinceCityId, request))
                .build();
    }

    @DeleteMapping("/{ProvinceCityId}")
    ApiResponse<String> deleteProvinceCity(@PathVariable Long ProvinceCityId) {
        ProvinceCityService.deleteProvinceCity(ProvinceCityId);
        return ApiResponse.<String>builder().result(" ProvinceCity has been deleted").build();
    }
}

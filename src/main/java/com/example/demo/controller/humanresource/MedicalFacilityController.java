package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.MedicalFacility.MedicalFacilityRequest;
import com.example.demo.dto.humanresource.MedicalFacility.MedicalFacilityResponse;
import com.example.demo.service.humanresource.MedicalFacilityService;
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
@RequestMapping("/medical-facilities")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MedicalFacilityController {
    MedicalFacilityService medicalFacilityService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<MedicalFacilityResponse> createMedicalFacility(@Valid @RequestBody MedicalFacilityRequest request) {
        ApiResponse<MedicalFacilityResponse> response = new ApiResponse<>();

        response.setResult(medicalFacilityService.createMedicalFacility(request));

        return response;
    }

    @PostMapping("/_bulk-upsert")
    ApiResponse<List<MedicalFacilityResponse>> bulkMedicalFacilityUpsert(@Valid @RequestBody List<MedicalFacilityRequest> requests) {
        return ApiResponse.<List<MedicalFacilityResponse>>builder()
                .result(medicalFacilityService.bulkUpsertMedicalFacilities(requests))
                .build();
    }

    @DeleteMapping("/_bulk-delete")
    public ApiResponse<String> bulkDeleteMedicalFacilities(@Valid @RequestParam("ids") List<Long> medicalFacilityIds) {
        medicalFacilityService.bulkDeleteMedicalFacilities(medicalFacilityIds);
        return ApiResponse.<String>builder()
                .result(medicalFacilityIds.size() + " medical Facilities have been deleted.")
                .build();
    }

    @GetMapping()
    ApiResponse<List<MedicalFacilityResponse>> getMedicalFacilities(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                                    @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                                    @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                                    @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<MedicalFacilityResponse>>builder()
                .result(medicalFacilityService.getMedicalFacilities(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{medicalFacilityId}")
    ApiResponse<MedicalFacilityResponse> getMedicalFacility(@PathVariable("medicalFacilityId") Long medicalFacilityId) {
        return ApiResponse.<MedicalFacilityResponse>builder()
                .result(medicalFacilityService.getMedicalFacility(medicalFacilityId))
                .build();
    }

    @PutMapping("/{medicalFacilityId}")
    ApiResponse<MedicalFacilityResponse> updateMedicalFacility(@PathVariable("medicalFacilityId") Long medicalFacilityId, @RequestBody MedicalFacilityRequest request) {
        return ApiResponse.<MedicalFacilityResponse>builder()
                .result(medicalFacilityService.updateMedicalFacility(medicalFacilityId, request))
                .build();
    }

    @DeleteMapping("/{medicalFacilityId}")
    ApiResponse<String> deleteMedicalFacility(@PathVariable Long medicalFacilityId) {
        medicalFacilityService.deleteMedicalFacility(medicalFacilityId);
        return ApiResponse.<String>builder().result("Medical Facility has been deleted").build();
    }
}

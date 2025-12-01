package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.Specialization.SpecializationRequest;
import com.example.demo.dto.humanresource.Specialization.SpecializationResponse;
import com.example.demo.service.humanresource.SpecializationService;
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
@RequestMapping("/specializations")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SpecializationController {
    SpecializationService specializationService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<SpecializationResponse> createSpecialization(@Valid @RequestBody SpecializationRequest request) {
        ApiResponse<SpecializationResponse> response = new ApiResponse<>();

        response.setResult(specializationService.createSpecialization(request));

        return response;
    }

    /**
     * BULK UPSERT ENDPOINT
     */
//    @PostMapping("/_bulk-upsert")
//    @ResponseStatus(HttpStatus.OK)
//    ApiResponse<List<SpecializationResponse>> bulkUpsertSpecializations(
//            @Valid @RequestBody List<SpecializationRequest> requests) {
//        return ApiResponse.<List<SpecializationResponse>>builder()
//                .result(specializationService.bulkUpsertSpecializations(requests))
//                .build();
//    }
//
//    /**
//     * BULK DELETE ENDPOINT
//     */
//    @DeleteMapping("/_bulk-delete")
//    @ResponseStatus(HttpStatus.OK)
//    ApiResponse<String> bulkDeleteSpecializations(@RequestParam("ids") List<Long> ids) {
//        specializationService.bulkDeleteSpecializations(ids);
//        return ApiResponse.<String>builder()
//                .result(ids.size() + " specializations have been deleted successfully")
//                .build();
//    }
    @GetMapping()
    ApiResponse<List<SpecializationResponse>> getSpecializations(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                                 @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                                 @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                                 @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<SpecializationResponse>>builder()
                .result(specializationService.getSpecializations(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{specializationId}")
    ApiResponse<SpecializationResponse> getSpecialization(@PathVariable("specializationId") Long specializationId) {
        return ApiResponse.<SpecializationResponse>builder()
                .result(specializationService.getSpecialization(specializationId))
                .build();
    }

    @PutMapping("/{specializationId}")
    ApiResponse<SpecializationResponse> updateSpecialization(@PathVariable("specializationId") Long specializationId, @RequestBody SpecializationRequest request) {
        return ApiResponse.<SpecializationResponse>builder()
                .result(specializationService.updateSpecialization(specializationId, request))
                .build();
    }

    @DeleteMapping("/{specializationId}")
    ApiResponse<String> deleteSpecialization(@PathVariable Long specializationId) {
        specializationService.deleteSpecialization(specializationId);
        return ApiResponse.<String>builder().result("Specialization has been deleted").build();
    }
}

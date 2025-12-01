package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.Nationality.NationalityRequest;
import com.example.demo.dto.humanresource.Nationality.NationalityResponse;
import com.example.demo.service.humanresource.NationalityService;
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
@RequestMapping("/nationalities")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NationalityController {
    NationalityService nationalityService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<NationalityResponse> createNationality(@Valid @RequestBody NationalityRequest request) {
        ApiResponse<NationalityResponse> response = new ApiResponse<>();

        response.setResult(nationalityService.createNationality(request));

        return response;
    }

    /**
     * BULK UPSERT ENDPOINT
     */
//    @PostMapping("/_bulk-upsert")
//    @ResponseStatus(HttpStatus.OK)
//    ApiResponse<List<NationalityResponse>> bulkUpsertNationalities(
//            @Valid @RequestBody List<NationalityRequest> requests) {
//        return ApiResponse.<List<NationalityResponse>>builder()
//                .result(nationalityService.bulkUpsertNationalities(requests))
//                .build();
//    }
//
//    /**
//     * BULK DELETE ENDPOINT
//     */
//    @DeleteMapping("/_bulk-delete")
//    @ResponseStatus(HttpStatus.OK)
//    ApiResponse<String> bulkDeleteNationalities(@RequestParam("ids") List<Long> ids) {
//        nationalityService.bulkDeleteNationalities(ids);
//        return ApiResponse.<String>builder()
//                .result(ids.size() + " nationalities have been deleted successfully")
//                .build();
//    }
    @GetMapping()
    ApiResponse<List<NationalityResponse>> getNationalities(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                            @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                            @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                            @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<NationalityResponse>>builder()
                .result(nationalityService.getNationalities(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{nationalityId}")
    ApiResponse<NationalityResponse> getNationality(@PathVariable("nationalityId") Long nationalityId) {
        return ApiResponse.<NationalityResponse>builder()
                .result(nationalityService.getNationality(nationalityId))
                .build();
    }

    @PutMapping("/{nationalityId}")
    ApiResponse<NationalityResponse> updateNationality(@PathVariable("nationalityId") Long nationalityId, @RequestBody NationalityRequest request) {
        return ApiResponse.<NationalityResponse>builder()
                .result(nationalityService.updateNationality(nationalityId, request))
                .build();
    }

    @DeleteMapping("/{nationalityId}")
    ApiResponse<String> deleteNationality(@PathVariable Long nationalityId) {
        nationalityService.deleteNationality(nationalityId);
        return ApiResponse.<String>builder().result("Nationality has been deleted").build();
    }
}

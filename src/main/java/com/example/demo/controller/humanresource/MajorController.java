package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.Major.MajorRequest;
import com.example.demo.dto.humanresource.Major.MajorResponse;
import com.example.demo.service.humanresource.MajorService;
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
@RequestMapping("/majors")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MajorController {
    MajorService majorService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<MajorResponse> createMajor(@Valid @RequestBody MajorRequest request) {
        ApiResponse<MajorResponse> response = new ApiResponse<>();

        response.setResult(majorService.createMajor(request));

        return response;
    }

    /**
     * BULK UPSERT ENDPOINT
     */
//    @PostMapping("/_bulk-upsert")
//    @ResponseStatus(HttpStatus.OK)
//    ApiResponse<List<MajorResponse>> bulkUpsertMajors(
//            @Valid @RequestBody List<MajorRequest> requests) {
//        return ApiResponse.<List<MajorResponse>>builder()
//                .result(majorService.bulkUpsertMajors(requests))
//                .build();
//    }
//
//    /**
//     * BULK DELETE ENDPOINT
//     */
//    @DeleteMapping("/_bulk-delete")
//    @ResponseStatus(HttpStatus.OK)
//    ApiResponse<String> bulkDeleteMajors(@RequestParam("ids") List<Long> ids) {
//        majorService.bulkDeleteMajors(ids);
//        return ApiResponse.<String>builder()
//                .result(ids.size() + " majors have been deleted successfully")
//                .build();
//    }
    @GetMapping()
    ApiResponse<List<MajorResponse>> getMajors(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                               @RequestParam(required = false, defaultValue = "5") int pageSize,
                                               @RequestParam(required = false, defaultValue = "name") String sortBy,
                                               @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<MajorResponse>>builder()
                .result(majorService.getMajors(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{majorId}")
    ApiResponse<MajorResponse> getMajor(@PathVariable("majorId") Long majorId) {
        return ApiResponse.<MajorResponse>builder()
                .result(majorService.getMajor(majorId))
                .build();
    }

    @PutMapping("/{majorId}")
    ApiResponse<MajorResponse> updateMajor(@PathVariable("majorId") Long majorId, @RequestBody MajorRequest request) {
        return ApiResponse.<MajorResponse>builder()
                .result(majorService.updateMajor(majorId, request))
                .build();
    }

    @DeleteMapping("/{majorId}")
    ApiResponse<String> deleteMajor(@PathVariable Long majorId) {
        majorService.deleteMajor(majorId);
        return ApiResponse.<String>builder().result("Major has been deleted").build();
    }
}

package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.OldDistrict.OldDistrictRequest;
import com.example.demo.dto.humanresource.OldDistrict.OldDistrictResponse;
import com.example.demo.service.humanresource.OldDistrictService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/old-districts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OldDistrictController {
    OldDistrictService oldDistrictService;

    @PostMapping()
    ApiResponse<OldDistrictResponse> createOldDistrict(@Valid @RequestBody OldDistrictRequest request) {
        ApiResponse<OldDistrictResponse> response = new ApiResponse<>();

        response.setResult(oldDistrictService.createOldDistrict(request));

        return response;
    }

    @GetMapping()
    ApiResponse<List<OldDistrictResponse>> getOldDistricts(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                           @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                           @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                           @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<OldDistrictResponse>>builder()
                .result(oldDistrictService.getOldDistricts(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{oldDistrictId}")
    ApiResponse<OldDistrictResponse> getOldDistrict(@PathVariable("oldDistrictId") Long oldDistrictId) {
        return ApiResponse.<OldDistrictResponse>builder()
                .result(oldDistrictService.getOldDistrict(oldDistrictId))
                .build();
    }

    @PutMapping("/{oldDistrictId}")
    ApiResponse<OldDistrictResponse> updateOldDistrict(@PathVariable("oldDistrictId") Long oldDistrictId, @RequestBody OldDistrictRequest request) {
        return ApiResponse.<OldDistrictResponse>builder()
                .result(oldDistrictService.updateOldDistrict(oldDistrictId, request))
                .build();
    }

    @DeleteMapping("/{oldDistrictId}")
    ApiResponse<String> deleteOldDistrict(@PathVariable Long oldDistrictId) {
        oldDistrictService.deleteOldDistrict(oldDistrictId);
        return ApiResponse.<String>builder().result("Old District has been deleted").build();
    }
}

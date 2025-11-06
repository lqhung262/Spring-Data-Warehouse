package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.EducationLevel.EducationLevelRequest;
import com.example.demo.dto.humanresource.EducationLevel.EducationLevelResponse;
import com.example.demo.service.humanresource.EducationLevelService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/education-levels")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EducationLevelController {
    EducationLevelService educationLevelService;

    @PostMapping()
    ApiResponse<EducationLevelResponse> createEducationLevel(@Valid @RequestBody EducationLevelRequest request) {
        ApiResponse<EducationLevelResponse> response = new ApiResponse<>();

        response.setResult(educationLevelService.createEducationLevel(request));

        return response;
    }

    @GetMapping()
    ApiResponse<List<EducationLevelResponse>> getEducationLevels(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                                 @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                                 @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                                 @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<EducationLevelResponse>>builder()
                .result(educationLevelService.getEducationLevels(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{educationLevelId}")
    ApiResponse<EducationLevelResponse> getEducationLevel(@PathVariable("educationLevelId") Long educationLevelId) {
        return ApiResponse.<EducationLevelResponse>builder()
                .result(educationLevelService.getEducationLevel(educationLevelId))
                .build();
    }

    @PutMapping("/{educationLevelId}")
    ApiResponse<EducationLevelResponse> updateEducationLevel(@PathVariable("educationLevelId") Long educationLevelId, @RequestBody EducationLevelRequest request) {
        return ApiResponse.<EducationLevelResponse>builder()
                .result(educationLevelService.updateEducationLevel(educationLevelId, request))
                .build();
    }

    @DeleteMapping("/{educationLevelId}")
    ApiResponse<String> deleteEducationLevel(@PathVariable Long educationLevelId) {
        educationLevelService.deleteEducationLevel(educationLevelId);
        return ApiResponse.<String>builder().result("Education Level has been deleted").build();
    }
}

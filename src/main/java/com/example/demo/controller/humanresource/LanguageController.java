package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.Language.LanguageRequest;
import com.example.demo.dto.humanresource.Language.LanguageResponse;
import com.example.demo.service.humanresource.LanguageService;
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
@RequestMapping("/languages")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LanguageController {
    LanguageService languageService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<LanguageResponse> createLanguage(@Valid @RequestBody LanguageRequest request) {
        ApiResponse<LanguageResponse> response = new ApiResponse<>();

        response.setResult(languageService.createLanguage(request));

        return response;
    }

    @PostMapping("/_bulk-upsert")
    ApiResponse<List<LanguageResponse>> bulkLanguageUpsert(@Valid @RequestBody List<LanguageRequest> requests) {
        return ApiResponse.<List<LanguageResponse>>builder()
                .result(languageService.bulkUpsertLanguages(requests))
                .build();
    }

    @DeleteMapping("/_bulk-delete")
    public ApiResponse<String> bulkDeleteLanguages(@Valid @RequestParam("ids") List<Long> languageIds) {
        languageService.bulkDeleteLanguages(languageIds);
        return ApiResponse.<String>builder()
                .result(languageIds.size() + " languages have been deleted.")
                .build();
    }

    @GetMapping()
    ApiResponse<List<LanguageResponse>> getLanguages(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                     @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                     @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                     @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<LanguageResponse>>builder()
                .result(languageService.getLanguages(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{languageId}")
    ApiResponse<LanguageResponse> getLanguage(@PathVariable("languageId") Long languageId) {
        return ApiResponse.<LanguageResponse>builder()
                .result(languageService.getLanguage(languageId))
                .build();
    }

    @PutMapping("/{languageId}")
    ApiResponse<LanguageResponse> updateLanguage(@PathVariable("languageId") Long languageId, @RequestBody LanguageRequest request) {
        return ApiResponse.<LanguageResponse>builder()
                .result(languageService.updateLanguage(languageId, request))
                .build();
    }

    @DeleteMapping("/{languageId}")
    ApiResponse<String> deleteLanguage(@PathVariable Long languageId) {
        languageService.deleteLanguage(languageId);
        return ApiResponse.<String>builder().result("Language has been deleted").build();
    }
}

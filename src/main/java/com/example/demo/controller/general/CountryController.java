package com.example.demo.controller.general;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.general.Country.CountryRequest;
import com.example.demo.dto.general.Country.CountryResponse;
import com.example.demo.service.general.CountryService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/countries")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CountryController {
    CountryService countryService;

    @PostMapping()
    ApiResponse<CountryResponse> createCountry(@Valid @RequestBody CountryRequest request) {
        ApiResponse<CountryResponse> response = new ApiResponse<>();

        response.setResult(countryService.createCountry(request));

        return response;
    }

    @GetMapping()
    ApiResponse<List<CountryResponse>> getCountries(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                    @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                    @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                    @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<CountryResponse>>builder()
                .result(countryService.getCountries(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{countryId}")
    ApiResponse<CountryResponse> getCountry(@PathVariable("countryId") Long countryId) {
        return ApiResponse.<CountryResponse>builder()
                .result(countryService.getCountry(countryId))
                .build();
    }

    @PutMapping("/{countryId}")
    ApiResponse<CountryResponse> updateCountry(@PathVariable("countryId") Long countryId, @RequestBody CountryRequest request) {
        return ApiResponse.<CountryResponse>builder()
                .result(countryService.updateCountry(countryId, request))
                .build();
    }

    @DeleteMapping("/{countryId}")
    ApiResponse<String> deleteCountry(@PathVariable Long countryId) {
        countryService.deleteCountry(countryId);
        return ApiResponse.<String>builder().result("Country has been deleted").build();
    }
}

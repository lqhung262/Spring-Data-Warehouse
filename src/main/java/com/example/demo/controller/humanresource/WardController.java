package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.Ward.WardRequest;
import com.example.demo.dto.humanresource.Ward.WardResponse;
import com.example.demo.service.humanresource.WardService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Wards")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WardController {
    WardService WardService;

    @PostMapping()
    ApiResponse<WardResponse> createWard(@Valid @RequestBody WardRequest request) {
        ApiResponse<WardResponse> response = new ApiResponse<>();

        response.setResult(WardService.createWard(request));

        return response;
    }

    @GetMapping()
    ApiResponse<List<WardResponse>> getWards(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                             @RequestParam(required = false, defaultValue = "5") int pageSize,
                                             @RequestParam(required = false, defaultValue = "name") String sortBy,
                                             @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<WardResponse>>builder()
                .result(WardService.getWards(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{WardId}")
    ApiResponse<WardResponse> getWard(@PathVariable("WardId") Long WardId) {
        return ApiResponse.<WardResponse>builder()
                .result(WardService.getWard(WardId))
                .build();
    }

    @PutMapping("/{WardId}")
    ApiResponse<WardResponse> updateWard(@PathVariable("WardId") Long WardId, @RequestBody WardRequest request) {
        return ApiResponse.<WardResponse>builder()
                .result(WardService.updateWard(WardId, request))
                .build();
    }

    @DeleteMapping("/{WardId}")
    ApiResponse<String> deleteWard(@PathVariable Long WardId) {
        WardService.deleteWard(WardId);
        return ApiResponse.<String>builder().result(" Ward has been deleted").build();
    }
}

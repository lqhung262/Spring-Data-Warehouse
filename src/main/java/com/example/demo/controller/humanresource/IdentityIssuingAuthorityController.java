package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.humanresource.IdentityIssuingAuthority.IdentityIssuingAuthorityRequest;
import com.example.demo.dto.humanresource.IdentityIssuingAuthority.IdentityIssuingAuthorityResponse;
import com.example.demo.service.humanresource.IdentityIssuingAuthorityService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.controller.humanresource.AttendanceMachineController.getBulkOperationResultApiResponse;

@RestController
@RequestMapping("/identity-issuing-authorities")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IdentityIssuingAuthorityController {
    IdentityIssuingAuthorityService identityIssuingAuthorityService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<IdentityIssuingAuthorityResponse> createIdentityIssuingAuthority(@Valid @RequestBody IdentityIssuingAuthorityRequest request) {
        ApiResponse<IdentityIssuingAuthorityResponse> response = new ApiResponse<>();

        response.setResult(identityIssuingAuthorityService.createIdentityIssuingAuthority(request));

        return response;
    }

    /**
     * BULK UPSERT ENDPOINT
     */
    @PostMapping("/_bulk-upsert")
    @ResponseStatus(HttpStatus.OK)
    ApiResponse<BulkOperationResult<IdentityIssuingAuthorityResponse>> bulkUpsertIdentityIssuingAuthorities(
            @Valid @RequestBody List<IdentityIssuingAuthorityRequest> requests) {

        BulkOperationResult<IdentityIssuingAuthorityResponse> result =
                identityIssuingAuthorityService.bulkUpsertIdentityIssuingAuthorities(requests);

        // Determine response code based on result
        int responseCode;
        if (!result.hasErrors()) {
            // Trường hợp 1: Không có lỗi nào -> Thành công toàn bộ
            responseCode = 1000;
        } else if (result.hasSuccess()) {
            // Trường hợp 2: Có lỗi NHƯNG cũng có thành công -> Thành công một phần (Multi-Status)
            responseCode = 207;
        } else {
            // Trường hợp 3: Có lỗi VÀ không có thành công nào -> Thất bại toàn bộ
            responseCode = 400;
        }

        return ApiResponse.<BulkOperationResult<IdentityIssuingAuthorityResponse>>builder()
                .code(responseCode)
                .message(result.getSummary())
                .result(result)
                .build();
    }

    /**
     * BULK DELETE
     */
    @DeleteMapping("/_bulk-delete")
    @ResponseStatus(HttpStatus.OK)
    ApiResponse<BulkOperationResult<Long>> bulkDeleteIdentityIssuingAuthorities(@RequestParam("ids") List<Long> ids) {

        BulkOperationResult<Long> result = identityIssuingAuthorityService.bulkDeleteIdentityIssuingAuthorities(ids);

        // Determine response code
        return getBulkOperationResultApiResponse(result);
    }

    @GetMapping()
    ApiResponse<List<IdentityIssuingAuthorityResponse>> getIdentityIssuingAuthorities(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                                                      @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                                                      @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                                                      @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<IdentityIssuingAuthorityResponse>>builder()
                .result(identityIssuingAuthorityService.getIdentityIssuingAuthorities(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{identityIssuingAuthorityId}")
    ApiResponse<IdentityIssuingAuthorityResponse> getIdentityIssuingAuthority(@PathVariable("identityIssuingAuthorityId") Long identityIssuingAuthorityId) {
        return ApiResponse.<IdentityIssuingAuthorityResponse>builder()
                .result(identityIssuingAuthorityService.getIdentityIssuingAuthority(identityIssuingAuthorityId))
                .build();
    }

    @PutMapping("/{identityIssuingAuthorityId}")
    ApiResponse<IdentityIssuingAuthorityResponse> updateIdentityIssuingAuthority(@PathVariable("identityIssuingAuthorityId") Long identityIssuingAuthorityId, @RequestBody IdentityIssuingAuthorityRequest request) {
        return ApiResponse.<IdentityIssuingAuthorityResponse>builder()
                .result(identityIssuingAuthorityService.updateIdentityIssuingAuthority(identityIssuingAuthorityId, request))
                .build();
    }

    @DeleteMapping("/{identityIssuingAuthorityId}")
    ApiResponse<String> deleteIdentityIssuingAuthority(@PathVariable Long identityIssuingAuthorityId) {
        identityIssuingAuthorityService.deleteIdentityIssuingAuthority(identityIssuingAuthorityId);
        return ApiResponse.<String>builder().result("Identity Issuing Authority has been deleted").build();
    }
}

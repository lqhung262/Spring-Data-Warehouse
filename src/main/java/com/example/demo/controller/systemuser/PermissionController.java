package com.example.demo.controller.systemuser;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.systemuser.Permission.PermissionRequest;
import com.example.demo.dto.systemuser.Permission.PermissionResponse;
import com.example.demo.service.systemuser.PermissionService;
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
@RequestMapping("/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionController {
    PermissionService permissionService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<PermissionResponse> createPermission(@Valid @RequestBody PermissionRequest request) {
        ApiResponse<PermissionResponse> response = new ApiResponse<>();

        response.setResult(permissionService.createPermission(request));

        return response;
    }

    @GetMapping()
    ApiResponse<List<PermissionResponse>> getPermissions(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                         @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                         @RequestParam(required = false, defaultValue = "shortName") String sortBy,
                                                         @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<PermissionResponse>>builder()
                .result(permissionService.getPermissions(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{permissionId}")
    ApiResponse<PermissionResponse> getPermission(@PathVariable("permissionId") Long permissionId) {
        return ApiResponse.<PermissionResponse>builder()
                .result(permissionService.getPermission(permissionId))
                .build();
    }

    @PutMapping("/{permissionId}")
    ApiResponse<PermissionResponse> updatePermission(@PathVariable("permissionId") Long permissionId, @RequestBody PermissionRequest request) {
        return ApiResponse.<PermissionResponse>builder()
                .result(permissionService.updatePermission(permissionId, request))
                .build();
    }

    @DeleteMapping("/{permissionId}")
    ApiResponse<String> deletePermission(@PathVariable Long permissionId) {
        permissionService.deletePermission(permissionId);
        return ApiResponse.<String>builder().result("Permission has been deleted").build();
    }
}

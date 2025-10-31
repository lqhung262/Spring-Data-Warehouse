package com.example.demo.controller.systemuser;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.systemuser.RolePermission.RolePermissionRequest;
import com.example.demo.dto.systemuser.RolePermission.RolePermissionResponse;
import com.example.demo.service.systemuser.RolePermissionService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rolePermissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RolePermissionController {
    RolePermissionService rolePermissionService;

    @PostMapping()
    ApiResponse<RolePermissionResponse> createRolePermission(@Valid @RequestBody RolePermissionRequest request) {
        ApiResponse<RolePermissionResponse> response = new ApiResponse<>();

        response.setResult(rolePermissionService.createRolePermission(request));

        return response;
    }

    @GetMapping()
    ApiResponse<List<RolePermissionResponse>> getRolePermissions(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                                 @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                                 @RequestParam(required = false, defaultValue = "rolePermissionId") String sortBy,
                                                                 @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<RolePermissionResponse>>builder()
                .result(rolePermissionService.getRolePermissions(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{rolePermissionId}")
    ApiResponse<RolePermissionResponse> getRolePermission(@PathVariable("rolePermissionId") Long rolePermissionId) {
        return ApiResponse.<RolePermissionResponse>builder()
                .result(rolePermissionService.getRolePermission(rolePermissionId))
                .build();
    }

    @PutMapping("/{rolePermissionId}")
    ApiResponse<RolePermissionResponse> updateRolePermission(@PathVariable("rolePermissionId") Long rolePermissionId, @RequestBody RolePermissionRequest request) {
        return ApiResponse.<RolePermissionResponse>builder()
                .result(rolePermissionService.updateRolePermission(rolePermissionId, request))
                .build();
    }

    @DeleteMapping("/{rolePermissionId}")
    ApiResponse<String> deleteRolePermission(@PathVariable Long rolePermissionId) {
        rolePermissionService.deleteRolePermission(rolePermissionId);
        return ApiResponse.<String>builder().result("Role Permission has been deleted").build();
    }
}

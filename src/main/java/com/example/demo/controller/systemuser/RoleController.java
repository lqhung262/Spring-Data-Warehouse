package com.example.demo.controller.systemuser;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.systemuser.Role.RoleRequest;
import com.example.demo.dto.systemuser.Role.RoleResponse;
import com.example.demo.service.systemuser.RoleService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
    RoleService roleService;

    @PostMapping()
    ApiResponse<RoleResponse> createRole(@Valid @RequestBody RoleRequest request) {
        ApiResponse<RoleResponse> response = new ApiResponse<>();

        response.setResult(roleService.createRole(request));

        return response;
    }

    @GetMapping()
    ApiResponse<List<RoleResponse>> getRoles(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                             @RequestParam(required = false, defaultValue = "5") int pageSize,
                                             @RequestParam(required = false, defaultValue = "shortName") String sortBy,
                                             @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<RoleResponse>>builder()
                .result(roleService.getRoles(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{roleId}")
    ApiResponse<RoleResponse> getRole(@PathVariable("roleId") Long roleId) {
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.getRole(roleId))
                .build();
    }

    @PutMapping("/{roleId}")
    ApiResponse<RoleResponse> updateRole(@PathVariable("roleId") Long roleId, @RequestBody RoleRequest request) {
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.updateRole(roleId, request))
                .build();
    }

    @DeleteMapping("/{roleId}")
    ApiResponse<String> deleteRole(@PathVariable Long roleId) {
        roleService.deleteRole(roleId);
        return ApiResponse.<String>builder().result("Role has been deleted").build();
    }
}

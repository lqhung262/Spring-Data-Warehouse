package com.example.demo.controller.systemuser;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.systemuser.UserRole.UserRoleRequest;
import com.example.demo.dto.systemuser.UserRole.UserRoleResponse;
import com.example.demo.service.systemuser.UserRoleService;
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
@RequestMapping("/user-roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserRoleController {
    UserRoleService userRoleService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<UserRoleResponse> createUserRole(@Valid @RequestBody UserRoleRequest request) {
        ApiResponse<UserRoleResponse> response = new ApiResponse<>();

        response.setResult(userRoleService.createUserRole(request));

        return response;
    }

    @GetMapping()
    ApiResponse<List<UserRoleResponse>> getUserRoles(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                     @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                     @RequestParam(required = false, defaultValue = "userRoleId") String sortBy,
                                                     @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<UserRoleResponse>>builder()
                .result(userRoleService.getUserRoles(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{userRoleId}")
    ApiResponse<UserRoleResponse> getUserRole(@PathVariable("userRoleId") Long userRoleId) {
        return ApiResponse.<UserRoleResponse>builder()
                .result(userRoleService.getUserRole(userRoleId))
                .build();
    }

    @PutMapping("/{userRoleId}")
    ApiResponse<UserRoleResponse> updateUserRole(@PathVariable("userRoleId") Long userRoleId, @RequestBody UserRoleRequest request) {
        return ApiResponse.<UserRoleResponse>builder()
                .result(userRoleService.updateUserRole(userRoleId, request))
                .build();
    }

    @DeleteMapping("/{userRoleId}")
    ApiResponse<String> deleteUserRole(@PathVariable Long userRoleId) {
        userRoleService.deleteUserRole(userRoleId);
        return ApiResponse.<String>builder().result("User Role has been deleted").build();
    }
}

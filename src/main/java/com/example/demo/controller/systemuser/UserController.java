package com.example.demo.controller.systemuser;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.systemuser.User.UserRequest;
import com.example.demo.dto.systemuser.User.UserResponse;
import com.example.demo.service.systemuser.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_DDC_SUPER_ADMIN') or hasRole('ROLE_DDC_HR_ADMIN')")
    public ApiResponse<UserResponse> createUser(@RequestBody UserRequest request) {
        // mặc định gán role từ Keycloak realm role name (ví dụ "ROLE_DDC_HR_VIEWER")
        String defaultRole = "ROLE_DDC_HR_VIEWER";
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request, defaultRole))
                .build();
    }
}

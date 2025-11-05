package com.example.demo.controller.systemuser;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.systemuser.User.UserRequest;
import com.example.demo.dto.systemuser.User.UserResponse;
import com.example.demo.service.systemuser.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ApiResponse<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        // mặc định gán role từ Keycloak realm role name (ví dụ "ROLE_DDC_HR_VIEWER")
        String defaultRole = "ROLE_DDC_HR_VIEWER";
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request, defaultRole))
                .build();
    }

    @GetMapping
    public ApiResponse<List<UserResponse>> getAllUsers(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                       @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                       @RequestParam(required = false, defaultValue = "userName") String sortBy,
                                                       @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getUsers(PageRequest.of(pageNo - 1, pageSize, sort)).getContent())
                .build();
    }

    @GetMapping("/{userId}")
    public ApiResponse<UserResponse> getUser(@PathVariable Long userId) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUser(userId))
                .build();
    }

    @PutMapping("/{userId}")
    public ApiResponse<UserResponse> updateUser(@PathVariable Long userId, @RequestBody UserRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(userId, request))
                .build();
    }

    @DeleteMapping("/{userId}")
    public ApiResponse<String> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ApiResponse.<String>builder()
                .result("User deleted")
                .build();
    }

    @GetMapping("/search")
    public ApiResponse<List<UserResponse>> searchUsers(@RequestParam("q") String q,
                                                       @RequestParam(required = false, defaultValue = "1") int pageNo,
                                                       @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                       @RequestParam(required = false, defaultValue = "userName") String sortBy,
                                                       @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.searchUsers(q, PageRequest.of(pageNo - 1, pageSize, sort)).getContent())
                .build();
    }
}

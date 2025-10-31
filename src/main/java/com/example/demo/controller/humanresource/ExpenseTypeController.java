package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.ExpenseType.ExpenseTypeRequest;
import com.example.demo.dto.humanresource.ExpenseType.ExpenseTypeResponse;
import com.example.demo.service.humanresource.ExpenseTypeService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/expenseTypes")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExpenseTypeController {
    ExpenseTypeService expenseTypeService;

    @PostMapping()
    ApiResponse<ExpenseTypeResponse> createExpenseType(@Valid @RequestBody ExpenseTypeRequest request) {
        ApiResponse<ExpenseTypeResponse> response = new ApiResponse<>();

        response.setResult(expenseTypeService.createExpenseType(request));

        return response;
    }

    @GetMapping()
    ApiResponse<List<ExpenseTypeResponse>> getExpenseTypes(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                           @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                           @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                           @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<ExpenseTypeResponse>>builder()
                .result(expenseTypeService.getExpenseTypes(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{expenseTypeId}")
    ApiResponse<ExpenseTypeResponse> getExpenseType(@PathVariable("expenseTypeId") Long expenseTypeId) {
        return ApiResponse.<ExpenseTypeResponse>builder()
                .result(expenseTypeService.getExpenseType(expenseTypeId))
                .build();
    }

    @PutMapping("/{expenseTypeId}")
    ApiResponse<ExpenseTypeResponse> updateExpenseType(@PathVariable("expenseTypeId") Long expenseTypeId, @RequestBody ExpenseTypeRequest request) {
        return ApiResponse.<ExpenseTypeResponse>builder()
                .result(expenseTypeService.updateExpenseType(expenseTypeId, request))
                .build();
    }

    @DeleteMapping("/{expenseTypeId}")
    ApiResponse<String> deleteExpenseType(@PathVariable Long expenseTypeId) {
        expenseTypeService.deleteExpenseType(expenseTypeId);
        return ApiResponse.<String>builder().result("Expense Type has been deleted").build();
    }
}

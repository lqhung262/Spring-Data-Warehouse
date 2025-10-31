package com.example.demo.dto.humanresource.ExpenseType;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseTypeRequest {
    @NotBlank
    private String expenseTypeCode;

    @NotBlank
    private String name;
}

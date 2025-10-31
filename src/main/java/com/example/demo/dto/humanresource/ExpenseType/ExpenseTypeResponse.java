package com.example.demo.dto.humanresource.ExpenseType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExpenseTypeResponse {
    Long expenseTypeId;
    String expenseTypeCode;
    String name;
}

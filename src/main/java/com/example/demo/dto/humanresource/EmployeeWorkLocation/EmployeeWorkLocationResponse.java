package com.example.demo.dto.humanresource.EmployeeWorkLocation;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeWorkLocationResponse {
    Long employeeWorkLocationId;
    Long employeeId;
    Long workLocationId;
}

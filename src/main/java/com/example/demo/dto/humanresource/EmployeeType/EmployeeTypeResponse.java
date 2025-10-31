package com.example.demo.dto.humanresource.EmployeeType;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeTypeResponse {
    Long employeeTypeId;
    String employeeTypeCode;
    String sourceId;
    String name;
}

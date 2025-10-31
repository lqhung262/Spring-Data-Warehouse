package com.example.demo.dto.humanresource.Department;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DepartmentResponse {
    Long departmentId;
    String departmentCode;
    String sourceId;
    String name;
}

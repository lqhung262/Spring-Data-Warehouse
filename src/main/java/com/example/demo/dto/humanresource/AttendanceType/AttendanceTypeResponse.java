package com.example.demo.dto.humanresource.AttendanceType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttendanceTypeResponse {
    Long attendanceTypeId;
    String attendanceTypeCode;
    String sourceId;
    String name;
}

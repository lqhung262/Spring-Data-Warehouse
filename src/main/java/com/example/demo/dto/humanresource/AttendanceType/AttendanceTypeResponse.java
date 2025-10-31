package com.example.demo.dto.humanresource.AttendanceType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttendanceTypeResponse {
    Long workShiftId;
    String workShiftCode;
    String sourceId;
    String name;
}

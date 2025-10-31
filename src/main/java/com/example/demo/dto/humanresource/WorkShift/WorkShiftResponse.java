package com.example.demo.dto.humanresource.WorkShift;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WorkShiftResponse {
    Long workShiftId;
    String workShiftCode;
    String sourceId;
    String name;
}

package com.example.demo.dto.humanresource.WorkShiftGroup;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WorkShiftGroupResponse {
    Long workShiftGroupId;
    String workShiftGroupCode;
    String sourceId;
    String name;
}

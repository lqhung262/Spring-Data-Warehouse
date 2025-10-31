package com.example.demo.dto.humanresource.AttendanceMachine;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttendanceMachineResponse {
    Long attendanceMachineId;
    String attendanceMachineCode;
    String sourceId;
    String name;
}

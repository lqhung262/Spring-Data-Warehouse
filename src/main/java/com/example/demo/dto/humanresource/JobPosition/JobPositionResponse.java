package com.example.demo.dto.humanresource.JobPosition;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobPositionResponse {
    Long jobPositionId;
    String jobPositionCode;
    String sourceId;
    String name;
}

package com.example.demo.dto.humanresource.Specialization;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpecializationResponse {
    Long specializationId;
    String specializationCode;
    String sourceId;
    String name;
}

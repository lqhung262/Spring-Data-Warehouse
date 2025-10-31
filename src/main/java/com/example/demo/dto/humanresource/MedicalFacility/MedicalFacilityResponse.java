package com.example.demo.dto.humanresource.MedicalFacility;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MedicalFacilityResponse {
    Long medicalFacilityId;
    String medicalFacilityCode;
    String sourceId;
    String name;
    Long provinceCityId;
}

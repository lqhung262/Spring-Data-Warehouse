package com.example.demo.dto.humanresource.OldProvinceCity;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OldProvinceCityResponse {
    Long oldProvinceCityId;
    Long provinceCityId;
    String sourceId;
    String name;
}

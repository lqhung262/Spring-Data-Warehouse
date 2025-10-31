package com.example.demo.dto.humanresource.ProvinceCity;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProvinceCityResponse {
    Long provinceCityId;
    String sourceId;
    String name;
}

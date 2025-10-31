package com.example.demo.dto.humanresource.OldDistrict;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OldDistrictResponse {
    Long oldDistrictId;
    Long wardId;
    String sourceId;
    Long oldProvinceCityId;
    String name;
}

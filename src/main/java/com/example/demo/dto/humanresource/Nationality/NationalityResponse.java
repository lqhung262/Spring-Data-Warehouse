package com.example.demo.dto.humanresource.Nationality;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NationalityResponse {
    Long nationalityId;
    String nationalityCode;
    String sourceId;
    String name;
}

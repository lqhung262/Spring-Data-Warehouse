package com.example.demo.dto.humanresource.Ward;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WardResponse {
    Long wardId;
    String sourceId;
    Long provinceCityId;
    String name;
}

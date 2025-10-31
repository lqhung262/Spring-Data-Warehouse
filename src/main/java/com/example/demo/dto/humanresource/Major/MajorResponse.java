package com.example.demo.dto.humanresource.Major;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MajorResponse {
    Long majorId;
    String majorCode;
    String sourceId;
    String name;
}

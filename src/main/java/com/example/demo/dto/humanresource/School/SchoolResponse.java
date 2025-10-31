package com.example.demo.dto.humanresource.School;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SchoolResponse {
    Long schoolId;
    String schoolCode;
    String sourceId;
    String name;
}

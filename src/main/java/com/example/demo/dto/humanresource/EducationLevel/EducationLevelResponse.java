package com.example.demo.dto.humanresource.EducationLevel;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EducationLevelResponse {
    Long educationLevelId;
    String educationLevelCode;
    String sourceId;
    String name;
}

package com.example.demo.dto.humanresource.Language;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LanguageResponse {
    Long languageId;
    String languageCode;
    String sourceId;
    String name;
}

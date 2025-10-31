package com.example.demo.dto.humanresource.OtType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OtTypeResponse {
    Long otTypeId;
    String otTypeCode;
    String name;
}

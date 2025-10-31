package com.example.demo.dto.general.SourceSystem;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SourceSystemResponse {
    Long sourceSystemId;
    String name;
    String description;
}

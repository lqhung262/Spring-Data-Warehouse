package com.example.demo.dto.humanresource.WorkLocation;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WorkLocationResponse {
    Long workLocationId;
    String workLocationCode;
    String name;
}

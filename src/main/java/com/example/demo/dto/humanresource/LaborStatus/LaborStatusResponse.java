package com.example.demo.dto.humanresource.LaborStatus;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LaborStatusResponse {
    Long laborStatusId;
    String laborStatusCode;
    String sourceId;
    String name;
}

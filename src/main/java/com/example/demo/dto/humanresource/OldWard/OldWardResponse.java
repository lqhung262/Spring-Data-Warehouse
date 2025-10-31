package com.example.demo.dto.humanresource.OldWard;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OldWardResponse {
    Long oldWardId;
    Long wardId;
    String sourceId;
    Long oldDistrictId;
    String name;
}

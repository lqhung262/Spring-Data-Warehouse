package com.example.demo.dto.humanresource.JobTitle;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobTitleResponse {
    Long jobTitleId;
    String jobTitleCode;
    String sourceId;
    String name;
}

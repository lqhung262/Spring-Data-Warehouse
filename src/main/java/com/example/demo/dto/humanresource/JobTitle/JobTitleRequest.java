package com.example.demo.dto.humanresource.JobTitle;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobTitleRequest {
    @NotBlank
    private String jobTitleCode;

    @NotBlank
    private String sourceId;

    @NotBlank
    private String name;
}

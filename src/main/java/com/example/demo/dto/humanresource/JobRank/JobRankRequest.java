package com.example.demo.dto.humanresource.JobRank;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobRankRequest {
    @NotBlank
    private String jobRankCode;

    @NotBlank
    private String sourceId;

    @NotBlank
    private String name;
}

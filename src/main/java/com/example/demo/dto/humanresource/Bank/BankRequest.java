package com.example.demo.dto.humanresource.Bank;

import com.example.demo.dto.common.BaseRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Request DTO for Bank operations.
 * Extends BaseRequest to inherit sourceId and sourceSystemId fields.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BankRequest extends BaseRequest {
    
    @NotBlank(message = "Bank code is required")
    private String bankCode;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Short name is required")
    private String shortName;
}

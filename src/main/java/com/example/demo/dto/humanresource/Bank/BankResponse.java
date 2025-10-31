package com.example.demo.dto.humanresource.Bank;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BankResponse {
    Long bankId;
    String bankCode;
    String sourceId;
    String name;
    String shortName;
}

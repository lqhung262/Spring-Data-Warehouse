package com.example.demo.dto.general.Country;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CountryResponse {
    Long countryId;
    String countryCode;
    String sourceId;
    String name;
}

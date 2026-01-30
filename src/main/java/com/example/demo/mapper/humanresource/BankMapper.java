package com.example.demo.mapper.humanresource;

import com.example.demo.dto.humanresource.Bank.BankRequest;
import com.example.demo.dto.humanresource.Bank.BankResponse;
import com.example.demo.entity.humanresource.Bank;
import com.example.demo.mapper.common.CommonMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * MapStruct mapper for Bank entity.
 * Uses CommonMapperConfig for shared settings (null handling, unmapped policy).
 */
@Mapper(config = CommonMapperConfig.class)
public interface BankMapper {
    
    Bank toBank(BankRequest request);

    @Mapping(target = "id", source = "bankId")
    @Mapping(target = "code", source = "bankCode")
    BankResponse toBankResponse(Bank bank);

    void updateBank(@MappingTarget Bank bank, BankRequest request);
}
package com.example.demo.mapper.humanresource;

import com.example.demo.dto.humanresource.Bank.BankRequest;
import com.example.demo.dto.humanresource.Bank.BankResponse;
import com.example.demo.entity.humanresource.Bank;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BankMapper {
    Bank toBank(BankRequest request);

    BankResponse toBankResponse(Bank bank);

    void updateBank(@MappingTarget Bank bank, BankRequest request);
}
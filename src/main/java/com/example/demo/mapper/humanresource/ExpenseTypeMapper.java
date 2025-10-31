package com.example.demo.mapper.humanresource;

import com.example.demo.dto.humanresource.ExpenseType.ExpenseTypeRequest;
import com.example.demo.dto.humanresource.ExpenseType.ExpenseTypeResponse;
import com.example.demo.entity.humanresource.ExpenseType;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ExpenseTypeMapper {
    ExpenseType toExpenseType(ExpenseTypeRequest request);

    ExpenseTypeResponse toExpenseTypeResponse(ExpenseType ExpenseType);

    void updateExpenseType(@MappingTarget ExpenseType ExpenseType, ExpenseTypeRequest request);
}

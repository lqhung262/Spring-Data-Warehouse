package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.ExpenseType.ExpenseTypeRequest;
import com.example.demo.dto.humanresource.ExpenseType.ExpenseTypeResponse;
import com.example.demo.entity.humanresource.ExpenseType;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.ExpenseTypeMapper;
import com.example.demo.repository.humanresource.ExpenseTypeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExpenseTypeService {
    final ExpenseTypeRepository expenseTypeRepository;
    final ExpenseTypeMapper expenseTypeMapper;

    @Value("${entities.humanresource.expensetype}")
    private String entityName;

    public ExpenseTypeResponse createExpenseType(ExpenseTypeRequest request) {
        ExpenseType expenseType = expenseTypeMapper.toExpenseType(request);

        return expenseTypeMapper.toExpenseTypeResponse(expenseTypeRepository.save(expenseType));
    }

    public List<ExpenseTypeResponse> getExpenseTypes(Pageable pageable) {
        Page<ExpenseType> page = expenseTypeRepository.findAll(pageable);
        List<ExpenseTypeResponse> dtos = page.getContent()
                .stream().map(expenseTypeMapper::toExpenseTypeResponse).toList();
        return dtos;
    }

    public ExpenseTypeResponse getExpenseType(Long id) {
        return expenseTypeMapper.toExpenseTypeResponse(expenseTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public ExpenseTypeResponse updateExpenseType(Long id, ExpenseTypeRequest request) {
        ExpenseType expenseType = expenseTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        expenseTypeMapper.updateExpenseType(expenseType, request);

        return expenseTypeMapper.toExpenseTypeResponse(expenseTypeRepository.save(expenseType));
    }

    public void deleteExpenseType(Long id) {
        ExpenseType expenseType = expenseTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        expenseTypeRepository.deleteById(id);
    }
}

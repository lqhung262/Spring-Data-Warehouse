package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.EmployeeType.EmployeeTypeRequest;
import com.example.demo.dto.humanresource.EmployeeType.EmployeeTypeResponse;
import com.example.demo.entity.humanresource.EmployeeType;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.EmployeeTypeMapper;
import com.example.demo.repository.humanresource.EmployeeTypeRepository;
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
public class EmployeeTypeService {
    final EmployeeTypeRepository employeeTypeRepository;
    final EmployeeTypeMapper employeeTypeMapper;

    @Value("${entities.humanresource.employeetype}")
    private String entityName;


    public EmployeeTypeResponse createEmployeeType(EmployeeTypeRequest request) {
        EmployeeType employeeType = employeeTypeMapper.toEmployeeType(request);

        return employeeTypeMapper.toEmployeeTypeResponse(employeeTypeRepository.save(employeeType));
    }

    public List<EmployeeTypeResponse> getEmployeeTypes(Pageable pageable) {
        Page<EmployeeType> page = employeeTypeRepository.findAll(pageable);
        List<EmployeeTypeResponse> dtos = page.getContent()
                .stream().map(employeeTypeMapper::toEmployeeTypeResponse).toList();
        return dtos;
    }

    public EmployeeTypeResponse getEmployeeType(Long id) {
        return employeeTypeMapper.toEmployeeTypeResponse(employeeTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public EmployeeTypeResponse updateEmployeeType(Long id, EmployeeTypeRequest request) {
        EmployeeType employeeType = employeeTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        employeeTypeMapper.updateEmployeeType(employeeType, request);

        return employeeTypeMapper.toEmployeeTypeResponse(employeeTypeRepository.save(employeeType));
    }

    public void deleteEmployeeType(Long id) {
        EmployeeType employeeType = employeeTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        employeeTypeRepository.deleteById(id);
    }
}

package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.Employee.EmployeeRequest;
import com.example.demo.dto.humanresource.Employee.EmployeeResponse;
import com.example.demo.entity.humanresource.Employee;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.EmployeeMapper;
import com.example.demo.repository.humanresource.EmployeeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeService {
    final EmployeeRepository employeeRepository;
    final EmployeeMapper employeeMapper;

    @Value("${entities.humanresource.employee}")
    private String entityName;

    public EmployeeResponse createEmployee(EmployeeRequest request) {
        Employee employee = employeeMapper.toEmployee(request);

        return employeeMapper.toEmployeeResponse(employeeRepository.save(employee));
    }

    public List<EmployeeResponse> getEmployees(Pageable pageable) {
        return employeeRepository.findAll(pageable).getContent().stream().map(employeeMapper::toEmployeeResponse).toList();
    }

    public EmployeeResponse getEmployee(Long id) {
        return employeeMapper.toEmployeeResponse(employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        employeeMapper.updateEmployee(employee, request);

        return employeeMapper.toEmployeeResponse(employeeRepository.save(employee));
    }

    public void deleteEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException(entityName));
        employeeRepository.deleteById(employeeId);
    }
}

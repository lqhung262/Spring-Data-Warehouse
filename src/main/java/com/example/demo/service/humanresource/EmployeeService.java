package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.Employee.EmployeeRequest;
import com.example.demo.dto.humanresource.Employee.EmployeeResponse;
import com.example.demo.entity.humanresource.Employee;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.EmployeeMapper;
import com.example.demo.repository.humanresource.EmployeeRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeService {
    final EmployeeRepository employeeRepository;
    final EmployeeMapper employeeMapper;

    @Value("${entities.humanresource.employee}")
    private String entityName;

    public EmployeeResponse createEmployee(EmployeeRequest request) {
        employeeRepository.findByEmployeeCode(request.getEmployeeCode()).ifPresent(b -> {
            throw new IllegalArgumentException(entityName + " with employee Code " + request.getEmployeeCode() + " already exists.");
        });

        Employee employee = employeeMapper.toEmployee(request);

        return employeeMapper.toEmployeeResponse(employeeRepository.save(employee));
    }

    /**
     * Xử lý Bulk Upsert
     */
    @Transactional
    public List<EmployeeResponse> bulkUpsertEmployees(List<EmployeeRequest> requests) {

        // Lấy tất cả employeeCodes từ request
        List<String> employeeCodes = requests.stream()
                .map(EmployeeRequest::getEmployeeCode)
                .toList();

        // Tìm tất cả các employee đã tồn tại TRONG 1 CÂU QUERY
        Map<String, Employee> existingEmployeesMap = employeeRepository.findByEmployeeCodeIn(employeeCodes).stream()
                .collect(Collectors.toMap(Employee::getEmployeeCode, employee -> employee));

        List<Employee> employeesToSave = new java.util.ArrayList<>();

        // Lặp qua danh sách request để quyết định UPDATE hay INSERT
        for (EmployeeRequest request : requests) {
            Employee employee = existingEmployeesMap.get(request.getEmployeeCode());

            if (employee != null) {
                // --- Logic UPDATE ---
                // Employee đã tồn tại -> Cập nhật
                employeeMapper.updateEmployee(employee, request);
                employeesToSave.add(employee);
            } else {
                // --- Logic INSERT ---
                // Employee chưa tồn tại -> Tạo mới
                Employee newEmployee = employeeMapper.toEmployee(request);
                employeesToSave.add(newEmployee);
            }
        }

        // Lưu tất cả (cả insert và update) TRONG 1 LỆNH
        List<Employee> savedEmployees = employeeRepository.saveAll(employeesToSave);

        // Map sang Response DTO và trả về
        return savedEmployees.stream()
                .map(employeeMapper::toEmployeeResponse)
                .toList();
    }

    /**
     * Xử lý Bulk Delete
     */
    @Transactional
    public void bulkDeleteEmployees(List<Long> ids) {
        // Kiểm tra xem có bao nhiêu ID tồn tại
        long existingCount = employeeRepository.countByEmployeeIdIn(ids);
        if (existingCount != ids.size()) {
            // Không phải tất cả ID đều tồn tại
            throw new NotFoundException("Some" + entityName + "s not found. Cannot complete bulk delete.");
        }

        // Xóa tất cả bằng ID trong 1 câu query (hiệu quả)
        employeeRepository.deleteAllById(ids);
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

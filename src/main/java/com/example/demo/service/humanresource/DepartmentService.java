package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.Department.DepartmentRequest;
import com.example.demo.dto.humanresource.Department.DepartmentResponse;
import com.example.demo.entity.humanresource.Department;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.DepartmentMapper;
import com.example.demo.repository.humanresource.DepartmentRepository;
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
public class DepartmentService {
    final DepartmentRepository departmentRepository;
    final DepartmentMapper departmentMapper;

    @Value("${entities.humanresource.department}")
    private String entityName;

    public DepartmentResponse createDepartment(DepartmentRequest request) {
        Department department = departmentMapper.toDepartment(request);

        return departmentMapper.toDepartmentResponse(departmentRepository.save(department));
    }

    public List<DepartmentResponse> getDepartments(Pageable pageable) {
        Page<Department> page = departmentRepository.findAll(pageable);
        List<DepartmentResponse> dtos = page.getContent()
                .stream().map(departmentMapper::toDepartmentResponse).toList();
        return dtos;
    }

    public DepartmentResponse getDepartment(Long id) {
        return departmentMapper.toDepartmentResponse(departmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public DepartmentResponse updateDepartment(Long id, DepartmentRequest request) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        departmentMapper.updateDepartment(department, request);

        return departmentMapper.toDepartmentResponse(departmentRepository.save(department));
    }

    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        departmentRepository.deleteById(id);
    }
}

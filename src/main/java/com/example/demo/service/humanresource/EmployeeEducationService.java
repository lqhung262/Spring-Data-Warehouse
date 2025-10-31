package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.EmployeeEducation.EmployeeEducationRequest;
import com.example.demo.dto.humanresource.EmployeeEducation.EmployeeEducationResponse;
import com.example.demo.entity.humanresource.EmployeeEducation;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.EmployeeEducationMapper;
import com.example.demo.repository.humanresource.EmployeeEducationRepository;
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
public class EmployeeEducationService {
    final EmployeeEducationRepository employeeEducationRepository;
    final EmployeeEducationMapper employeeEducationMapper;

    @Value("${entities.humanresource.employeeeducation}")
    private String entityName;

    public EmployeeEducationResponse createEmployeeEducation(EmployeeEducationRequest request) {
        EmployeeEducation employeeEducation = employeeEducationMapper.toEmployeeEducation(request);

        return employeeEducationMapper.toEmployeeEducationResponse(employeeEducationRepository.save(employeeEducation));
    }

    public List<EmployeeEducationResponse> getEmployeeEducations(Pageable pageable) {
        Page<EmployeeEducation> page = employeeEducationRepository.findAll(pageable);
        List<EmployeeEducationResponse> dtos = page.getContent()
                .stream().map(employeeEducationMapper::toEmployeeEducationResponse).toList();
        return dtos;
    }

    public EmployeeEducationResponse getEmployeeEducation(Long id) {
        return employeeEducationMapper.toEmployeeEducationResponse(employeeEducationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public EmployeeEducationResponse updateEmployeeEducation(Long id, EmployeeEducationRequest request) {
        EmployeeEducation employeeEducation = employeeEducationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        employeeEducationMapper.updateEmployeeEducation(employeeEducation, request);

        return employeeEducationMapper.toEmployeeEducationResponse(employeeEducationRepository.save(employeeEducation));
    }

    public void deleteEmployeeEducation(Long id) {
        EmployeeEducation employeeEducation = employeeEducationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        employeeEducationRepository.deleteById(id);
    }
}

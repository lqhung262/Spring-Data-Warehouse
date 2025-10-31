package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.EmployeeWorkLocation.EmployeeWorkLocationRequest;
import com.example.demo.dto.humanresource.EmployeeWorkLocation.EmployeeWorkLocationResponse;
import com.example.demo.entity.humanresource.EmployeeWorkLocation;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.EmployeeWorkLocationMapper;
import com.example.demo.repository.humanresource.EmployeeWorkLocationRepository;
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
public class EmployeeWorkLocationService {
    final EmployeeWorkLocationRepository employeeWorkLocationRepository;
    final EmployeeWorkLocationMapper employeeWorkLocationMapper;

    @Value("${entities.humanresource.employeeworklocation}")
    private String entityName;

    public EmployeeWorkLocationResponse createEmployeeWorkLocation(EmployeeWorkLocationRequest request) {
        EmployeeWorkLocation employeeWorkLocation = employeeWorkLocationMapper.toEmployeeWorkLocation(request);

        return employeeWorkLocationMapper.toEmployeeWorkLocationResponse(employeeWorkLocationRepository.save(employeeWorkLocation));
    }

    public List<EmployeeWorkLocationResponse> getEmployeeWorkLocations(Pageable pageable) {
        Page<EmployeeWorkLocation> page = employeeWorkLocationRepository.findAll(pageable);
        List<EmployeeWorkLocationResponse> dtos = page.getContent()
                .stream().map(employeeWorkLocationMapper::toEmployeeWorkLocationResponse).toList();
        return dtos;
    }

    public EmployeeWorkLocationResponse getEmployeeWorkLocation(Long id) {
        return employeeWorkLocationMapper.toEmployeeWorkLocationResponse(employeeWorkLocationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public EmployeeWorkLocationResponse updateEmployeeWorkLocation(Long id, EmployeeWorkLocationRequest request) {
        EmployeeWorkLocation employeeWorkLocation = employeeWorkLocationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        employeeWorkLocationMapper.updateEmployeeWorkLocation(employeeWorkLocation, request);

        return employeeWorkLocationMapper.toEmployeeWorkLocationResponse(employeeWorkLocationRepository.save(employeeWorkLocation));
    }

    public void deleteEmployeeWorkLocation(Long id) {
        EmployeeWorkLocation employeeWorkLocation = employeeWorkLocationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        employeeWorkLocationRepository.deleteById(id);
    }

}

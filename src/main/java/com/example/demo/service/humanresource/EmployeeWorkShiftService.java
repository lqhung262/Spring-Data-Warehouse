package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.EmployeeWorkShift.EmployeeWorkShiftRequest;
import com.example.demo.dto.humanresource.EmployeeWorkShift.EmployeeWorkShiftResponse;
import com.example.demo.entity.humanresource.EmployeeWorkShift;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.EmployeeWorkShiftMapper;
import com.example.demo.repository.humanresource.EmployeeWorkShiftRepository;
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
public class EmployeeWorkShiftService {
    final EmployeeWorkShiftRepository employeeWorkShiftRepository;
    final EmployeeWorkShiftMapper employeeWorkShiftMapper;

    @Value("${entities.humanresource.employeeworkshift}")
    private String entityName;

    public EmployeeWorkShiftResponse createEmployeeWorkShift(EmployeeWorkShiftRequest request) {
        EmployeeWorkShift employeeWorkShift = employeeWorkShiftMapper.toEmployeeWorkShift(request);

        return employeeWorkShiftMapper.toEmployeeWorkShiftResponse(employeeWorkShiftRepository.save(employeeWorkShift));
    }

    public List<EmployeeWorkShiftResponse> getEmployeeWorkShifts(Pageable pageable) {
        return employeeWorkShiftRepository.findAll(pageable).getContent().stream().map(employeeWorkShiftMapper::toEmployeeWorkShiftResponse).toList();
    }

    public EmployeeWorkShiftResponse getEmployeeWorkShift(Long id) {
        return employeeWorkShiftMapper.toEmployeeWorkShiftResponse(employeeWorkShiftRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public EmployeeWorkShiftResponse updateEmployeeWorkShift(Long id, EmployeeWorkShiftRequest request) {
        EmployeeWorkShift employeeWorkShift = employeeWorkShiftRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        employeeWorkShiftMapper.updateEmployeeWorkShift(employeeWorkShift, request);

        return employeeWorkShiftMapper.toEmployeeWorkShiftResponse(employeeWorkShiftRepository.save(employeeWorkShift));
    }

    public void deleteEmployeeWorkShift(Long employeeId) {
        EmployeeWorkShift employeeWorkShift = employeeWorkShiftRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException(entityName));
        employeeWorkShiftRepository.deleteById(employeeId);
    }
}

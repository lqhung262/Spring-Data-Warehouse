package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.EmployeeAttendanceMachine.EmployeeAttendanceMachineRequest;
import com.example.demo.dto.humanresource.EmployeeAttendanceMachine.EmployeeAttendanceMachineResponse;
import com.example.demo.entity.humanresource.EmployeeAttendanceMachine;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.EmployeeAttendanceMachineMapper;
import com.example.demo.repository.humanresource.EmployeeAttendanceMachineRepository;
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
public class EmployeeAttendanceMachineService {
    final EmployeeAttendanceMachineRepository employeeAttendanceMachineRepository;
    final EmployeeAttendanceMachineMapper employeeAttendanceMachineMapper;

    @Value("${entities.humanresource.employeeattendancemachine}")
    private String entityName;

    public EmployeeAttendanceMachineResponse createEmployeeAttendanceMachine(EmployeeAttendanceMachineRequest request) {
        EmployeeAttendanceMachine employeeAttendanceMachine = employeeAttendanceMachineMapper.toEmployeeAttendanceMachine(request);

        return employeeAttendanceMachineMapper.toEmployeeAttendanceMachineResponse(employeeAttendanceMachineRepository.save(employeeAttendanceMachine));
    }

    public List<EmployeeAttendanceMachineResponse> getEmployeeAttendanceMachines(Pageable pageable) {
        Page<EmployeeAttendanceMachine> page = employeeAttendanceMachineRepository.findAll(pageable);
        List<EmployeeAttendanceMachineResponse> dtos = page.getContent()
                .stream().map(employeeAttendanceMachineMapper::toEmployeeAttendanceMachineResponse).toList();
        return dtos;
    }

    public EmployeeAttendanceMachineResponse getEmployeeAttendanceMachine(Long id) {
        return employeeAttendanceMachineMapper.toEmployeeAttendanceMachineResponse(employeeAttendanceMachineRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public EmployeeAttendanceMachineResponse updateEmployeeAttendanceMachine(Long id, EmployeeAttendanceMachineRequest request) {
        EmployeeAttendanceMachine employeeAttendanceMachine = employeeAttendanceMachineRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        employeeAttendanceMachineMapper.updateEmployeeAttendanceMachine(employeeAttendanceMachine, request);

        return employeeAttendanceMachineMapper.toEmployeeAttendanceMachineResponse(employeeAttendanceMachineRepository.save(employeeAttendanceMachine));
    }

    public void deleteEmployeeAttendanceMachine(Long id) {
        EmployeeAttendanceMachine employeeAttendanceMachine = employeeAttendanceMachineRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        employeeAttendanceMachineRepository.deleteById(id);
    }

}

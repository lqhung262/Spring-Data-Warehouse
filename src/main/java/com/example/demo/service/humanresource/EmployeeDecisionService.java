package com.example.demo.service.humanresource;


import com.example.demo.dto.humanresource.EmployeeDecision.EmployeeDecisionRequest;
import com.example.demo.dto.humanresource.EmployeeDecision.EmployeeDecisionResponse;
import com.example.demo.entity.humanresource.EmployeeDecision;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.EmployeeDecisionMapper;
import com.example.demo.repository.humanresource.EmployeeDecisionRepository;
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
public class EmployeeDecisionService {
    final EmployeeDecisionRepository employeeDecisionRepository;
    final EmployeeDecisionMapper employeeDecisionMapper;

    @Value("${entities.humanresource.employeedecision}")
    private String entityName;


    public EmployeeDecisionResponse createEmployeeDecision(EmployeeDecisionRequest request) {
        EmployeeDecision employeeDecision = employeeDecisionMapper.toEmployeeDecision(request);

        return employeeDecisionMapper.toEmployeeDecisionResponse(employeeDecisionRepository.save(employeeDecision));
    }

    public List<EmployeeDecisionResponse> getEmployeeDecisions(Pageable pageable) {
        return employeeDecisionRepository.findAll(pageable).getContent().stream().map(employeeDecisionMapper::toEmployeeDecisionResponse).toList();
    }

    public EmployeeDecisionResponse getEmployeeDecision(Long id) {
        return employeeDecisionMapper.toEmployeeDecisionResponse(employeeDecisionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public EmployeeDecisionResponse updateEmployeeDecision(Long id, EmployeeDecisionRequest request) {
        EmployeeDecision decision = employeeDecisionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        employeeDecisionMapper.updateEmployeeDecision(decision, request);

        return employeeDecisionMapper.toEmployeeDecisionResponse(employeeDecisionRepository.save(decision));
    }

    public void deleteEmployeeDecision(Long employeeDecisionId) {
        EmployeeDecision decision = employeeDecisionRepository.findById(employeeDecisionId)
                .orElseThrow(() -> new NotFoundException(entityName));
        employeeDecisionRepository.deleteById(employeeDecisionId);
    }

}

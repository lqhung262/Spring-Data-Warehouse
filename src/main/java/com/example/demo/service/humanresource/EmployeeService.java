package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.Employee.EmployeeRequest;
import com.example.demo.dto.humanresource.Employee.EmployeeResponse;
import com.example.demo.dto.humanresource.EmployeeAttendanceMachine.EmployeeAttendanceMachineRequest;
import com.example.demo.dto.humanresource.EmployeeDecision.EmployeeDecisionRequest;
import com.example.demo.dto.humanresource.EmployeeEducation.EmployeeEducationRequest;
import com.example.demo.dto.humanresource.EmployeeWorkLocation.EmployeeWorkLocationRequest;
import com.example.demo.dto.humanresource.EmployeeWorkShift.EmployeeWorkShiftRequest;
import com.example.demo.entity.humanresource.*;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.*;
import com.example.demo.repository.humanresource.*;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeService {
    final EmployeeRepository employeeRepository;
    final EmployeeMapper employeeMapper;

    final EmployeeDecisionRepository employeeDecisionRepository;
    final EmployeeEducationRepository employeeEducationRepository;
    final EmployeeAttendanceMachineRepository employeeAttendanceMachineRepository;
    final EmployeeWorkLocationRepository employeeWorkLocationRepository;
    final EmployeeWorkShiftRepository employeeWorkShiftRepository;

    final EmployeeDecisionMapper employeeDecisionMapper;
    final EmployeeEducationMapper employeeEducationMapper;
    final EmployeeAttendanceMachineMapper employeeAttendanceMachineMapper;
    final EmployeeWorkLocationMapper employeeWorkLocationMapper;
    final EmployeeWorkShiftMapper employeeWorkShiftMapper;

    @Value("${entities.humanresource.employee}")
    private String entityName;

    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        // check unique sourceId for Employee
        employeeRepository.findBySourceId(request.getSourceId()).ifPresent(e -> {
            throw new IllegalArgumentException(entityName + " with sourceId " + request.getSourceId() + " already exists.");
        });

        Employee employee = employeeMapper.toEmployee(request);
        if (employee.getCreatedBy() == null) employee.setCreatedBy(1L);
        if (employee.getUpdatedBy() == null) employee.setUpdatedBy(1L);

        Employee saved = employeeRepository.save(employee);

        // delegate child creation to helpers (these will validate duplicates)
        createDecisions(saved, request.getEmployeeDecisions());
        createEducations(saved, request.getEmployeeEducations());
        createAttendanceMachines(saved, request.getEmployeeAttendanceMachines());
        createWorkLocations(saved, request.getEmployeeWorkLocations());
        createOrUpdateWorkShift(saved, request.getEmployeeWorkShift());

        Employee loaded = employeeRepository.findById(saved.getId()).orElseThrow(() -> new NotFoundException(entityName));
        return employeeMapper.toEmployeeResponse(loaded);
    }

    public List<EmployeeResponse> getEmployees(Pageable pageable) {
        return employeeRepository.findAll(pageable).getContent().stream().map(employeeMapper::toEmployeeResponse).toList();
    }

    public EmployeeResponse getEmployee(Long id) {
        return employeeMapper.toEmployeeResponse(employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // check sourceId uniqueness if changing
        if (request.getSourceId() != null && !request.getSourceId().equals(employee.getSourceId())) {
            employeeRepository.findBySourceId(request.getSourceId()).ifPresent(e -> {
                throw new IllegalArgumentException(entityName + " with sourceId " + request.getSourceId() + " already exists.");
            });
        }

        // update employee fields
        employeeMapper.updateEmployee(employee, request);
        Employee saved = employeeRepository.save(employee);

        // replace child collections if provided
        if (request.getEmployeeDecisions() != null) replaceDecisions(saved, request.getEmployeeDecisions());
        if (request.getEmployeeEducations() != null) replaceEducations(saved, request.getEmployeeEducations());
        if (request.getEmployeeAttendanceMachines() != null)
            replaceAttendanceMachines(saved, request.getEmployeeAttendanceMachines());
        if (request.getEmployeeWorkLocations() != null) replaceWorkLocations(saved, request.getEmployeeWorkLocations());
        if (request.getEmployeeWorkShift() != null) createOrUpdateWorkShift(saved, request.getEmployeeWorkShift());

        Employee loaded = employeeRepository.findById(saved.getId()).orElseThrow(() -> new NotFoundException(entityName));
        return employeeMapper.toEmployeeResponse(loaded);
    }

    public void deleteEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException(entityName));

        // delete child records first
        List<EmployeeDecision> decisions = employeeDecisionRepository.findByEmployee_Id(employeeId);
        if (!decisions.isEmpty()) employeeDecisionRepository.deleteAll(decisions);

        List<EmployeeEducation> educations = employeeEducationRepository.findByEmployee_Id(employeeId);
        if (!educations.isEmpty()) employeeEducationRepository.deleteAll(educations);

        List<EmployeeAttendanceMachine> machines = employeeAttendanceMachineRepository.findByEmployee_Id(employeeId);
        if (!machines.isEmpty()) employeeAttendanceMachineRepository.deleteAll(machines);

        List<EmployeeWorkLocation> locations = employeeWorkLocationRepository.findByEmployee_Id(employeeId);
        if (!locations.isEmpty()) employeeWorkLocationRepository.deleteAll(locations);

        List<EmployeeWorkShift> shifts = employeeWorkShiftRepository.findByEmployee_Id(employeeId);
        if (!shifts.isEmpty()) employeeWorkShiftRepository.deleteAll(shifts);

        // finally delete employee
        employeeRepository.deleteById(employeeId);
    }

    // --- helpers ---
    private void createDecisions(Employee employee, Set<EmployeeDecisionRequest> decisions) {
        if (decisions == null || decisions.isEmpty()) return;
        // check duplicates in input decisionNo
        Set<String> seen = new HashSet<>();
        for (EmployeeDecisionRequest d : decisions) {
            if (!seen.add(d.getDecisionNo()))
                throw new IllegalArgumentException("Duplicate decisionNo in request: " + d.getDecisionNo());
            // check existing in DB
            employeeDecisionRepository.findByDecisionNo(d.getDecisionNo()).ifPresent(x -> {
                throw new IllegalArgumentException("Employee Decision with decisionNo " + d.getDecisionNo() + " already exists.");
            });
            // check composite uniqueness per employee (department, employeeType, jobPosition, jobTitle, jobRank, costCategoryLevel1)
            employeeDecisionRepository.findByEmployee_IdAndDepartmentIdAndEmployeeTypeIdAndJobPositionIdAndJobTitleIdAndJobRankIdAndCostCategoryLevel1(employee.getId(), d.getDepartmentId(), d.getEmployeeTypeId(), d.getJobPositionId(), d.getJobTitleId(), d.getJobRankId(), d.getCostCategoryLevel1()).ifPresent(x -> {
                throw new IllegalArgumentException("Employee Decision with same role/department combination already exists for employee " + employee.getId());
            });
            EmployeeDecision dec = employeeDecisionMapper.toEmployeeDecision(d);
            dec.setEmployee(employee);
            employeeDecisionRepository.save(dec);
        }
    }

    private void replaceDecisions(Employee employee, Set<EmployeeDecisionRequest> decisions) {
        // delete existing
        List<EmployeeDecision> existing = employeeDecisionRepository.findByEmployee_Id(employee.getId());
        if (!existing.isEmpty()) employeeDecisionRepository.deleteAll(existing);
        // create new
        createDecisions(employee, decisions);
    }

    private void createEducations(Employee employee, Set<EmployeeEducationRequest> educations) {
        if (educations == null || educations.isEmpty()) return;
        Set<String> seen = new HashSet<>();
        for (EmployeeEducationRequest e : educations) {
            String key = e.getMajorId() + "|" + e.getSpecializationId() + "|" + e.getEducationLevelId() + "|" + e.getSchoolId();
            if (!seen.add(key)) throw new IllegalArgumentException("Duplicate education combo in request: " + key);
            employeeEducationRepository.findByEmployee_IdAndMajorIdAndSpecializationIdAndEducationLevelIdAndSchoolId(employee.getId(), e.getMajorId(), e.getSpecializationId(), e.getEducationLevelId(), e.getSchoolId()).ifPresent(x -> {
                throw new IllegalArgumentException("Employee Education with same child ids already exists for employee " + employee.getId());
            });
            EmployeeEducation ee = employeeEducationMapper.toEmployeeEducation(e);
            ee.setEmployee(employee);
            employeeEducationRepository.save(ee);
        }
    }

    private void replaceEducations(Employee employee, Set<EmployeeEducationRequest> educations) {
        List<EmployeeEducation> existing = employeeEducationRepository.findByEmployee_Id(employee.getId());
        if (!existing.isEmpty()) employeeEducationRepository.deleteAll(existing);
        createEducations(employee, educations);
    }

    private void createAttendanceMachines(Employee employee, Set<EmployeeAttendanceMachineRequest> machines) {
        if (machines == null || machines.isEmpty()) return;
        Set<Long> seen = new HashSet<>();
        for (EmployeeAttendanceMachineRequest m : machines) {
            if (!seen.add(m.getMachineId()))
                throw new IllegalArgumentException("Duplicate machineId in request: " + m.getMachineId());
            employeeAttendanceMachineRepository.findByEmployee_IdAndMachineId(employee.getId(), m.getMachineId()).ifPresent(x -> {
                throw new IllegalArgumentException("Employee Attendance Machine with machineId " + m.getMachineId() + " already exists for employee " + employee.getId());
            });
            EmployeeAttendanceMachine eam = employeeAttendanceMachineMapper.toEmployeeAttendanceMachine(m);
            eam.setEmployee(employee);
            employeeAttendanceMachineRepository.save(eam);
        }
    }

    private void replaceAttendanceMachines(Employee employee, Set<EmployeeAttendanceMachineRequest> machines) {
        List<EmployeeAttendanceMachine> existing = employeeAttendanceMachineRepository.findByEmployee_Id(employee.getId());
        if (!existing.isEmpty()) employeeAttendanceMachineRepository.deleteAll(existing);
        createAttendanceMachines(employee, machines);
    }

    private void createWorkLocations(Employee employee, Set<EmployeeWorkLocationRequest> locations) {
        if (locations == null || locations.isEmpty()) return;
        Set<Long> seen = new HashSet<>();
        for (EmployeeWorkLocationRequest l : locations) {
            if (!seen.add(l.getWorkLocationId()))
                throw new IllegalArgumentException("Duplicate workLocationId in request: " + l.getWorkLocationId());
            employeeWorkLocationRepository.findByEmployee_IdAndWorkLocationId(employee.getId(), l.getWorkLocationId()).ifPresent(x -> {
                throw new IllegalArgumentException("Employee Work Location with workLocationId " + l.getWorkLocationId() + " already exists for employee " + employee.getId());
            });
            EmployeeWorkLocation ewl = employeeWorkLocationMapper.toEmployeeWorkLocation(l);
            ewl.setEmployee(employee);
            employeeWorkLocationRepository.save(ewl);
        }
    }

    private void replaceWorkLocations(Employee employee, Set<EmployeeWorkLocationRequest> locations) {
        List<EmployeeWorkLocation> existing = employeeWorkLocationRepository.findByEmployee_Id(employee.getId());
        if (!existing.isEmpty()) employeeWorkLocationRepository.deleteAll(existing);
        createWorkLocations(employee, locations);
    }

    private void createOrUpdateWorkShift(Employee employee, EmployeeWorkShiftRequest wsReq) {
        if (wsReq == null) return;
        // check composite uniqueness per employee
        employeeWorkShiftRepository.findByEmployee_IdAndAttendanceTypeIdAndWorkShiftIdAndOtTypeIdAndWorkShiftGroupId(employee.getId(), wsReq.getAttendanceTypeId(), wsReq.getWorkShiftId(), wsReq.getOtTypeId(), wsReq.getWorkShiftGroupId()).ifPresent(x -> {
            // if found, update it
            EmployeeWorkShift shift = x;
            employeeWorkShiftMapper.updateEmployeeWorkShift(shift, wsReq);
            shift.setEmployee(employee);
            employeeWorkShiftRepository.save(shift);
            return;
        });

        EmployeeWorkShift shift = employeeWorkShiftMapper.toEmployeeWorkShift(wsReq);
        shift.setEmployee(employee);
        employeeWorkShiftRepository.save(shift);
    }
}

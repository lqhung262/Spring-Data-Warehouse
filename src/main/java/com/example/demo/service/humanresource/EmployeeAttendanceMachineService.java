package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.EmployeeAttendanceMachine.EmployeeAttendanceMachineRequest;
import com.example.demo.dto.humanresource.EmployeeAttendanceMachine.EmployeeAttendanceMachineResponse;
import com.example.demo.entity.humanresource.EmployeeAttendanceMachine;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.EmployeeAttendanceMachineMapper;
import com.example.demo.repository.humanresource.EmployeeAttendanceMachineRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

//    /**
//     * Xử lý Bulk Upsert
//     */
//    @Transactional
//    public List<EmployeeAttendanceMachineResponse> bulkUpsertEmployeeAttendanceMachines(List<EmployeeAttendanceMachineRequest> requests) {
//
//        // Lấy tất cả employeeAttendanceMachineCodes từ request
//        List<String> employeeAttendanceMachineCodes = requests.stream()
//                .map(EmployeeAttendanceMachineRequest::getEmployeeAttendanceMachineCode)
//                .toList();
//
//        // Tìm tất cả các employeeAttendanceMachine đã tồn tại TRONG 1 CÂU QUERY
//        Map<String, EmployeeAttendanceMachine> existingEmployeeAttendanceMachinesMap = employeeAttendanceMachineRepository.findByEmployeeAttendanceMachineCodeIn(employeeAttendanceMachineCodes).stream()
//                .collect(Collectors.toMap(EmployeeAttendanceMachine::getEmployeeAttendanceMachineCode, employeeAttendanceMachine -> employeeAttendanceMachine));
//
//        List<EmployeeAttendanceMachine> employeeAttendanceMachinesToSave = new java.util.ArrayList<>();
//
//        // Lặp qua danh sách request để quyết định UPDATE hay INSERT
//        for (EmployeeAttendanceMachineRequest request : requests) {
//            EmployeeAttendanceMachine employeeAttendanceMachine = existingEmployeeAttendanceMachinesMap.get(request.getEmployeeAttendanceMachineCode());
//
//            if (employeeAttendanceMachine != null) {
//                // --- Logic UPDATE ---
//                // EmployeeAttendanceMachine đã tồn tại -> Cập nhật
//                employeeAttendanceMachineMapper.updateEmployeeAttendanceMachine(employeeAttendanceMachine, request);
//                employeeAttendanceMachinesToSave.add(employeeAttendanceMachine);
//            } else {
//                // --- Logic INSERT ---
//                // EmployeeAttendanceMachine chưa tồn tại -> Tạo mới
//                EmployeeAttendanceMachine newEmployeeAttendanceMachine = employeeAttendanceMachineMapper.toEmployeeAttendanceMachine(request);
//                employeeAttendanceMachinesToSave.add(newEmployeeAttendanceMachine);
//            }
//        }
//
//        // Lưu tất cả (cả insert và update) TRONG 1 LỆNH
//        List<EmployeeAttendanceMachine> savedEmployeeAttendanceMachines = employeeAttendanceMachineRepository.saveAll(employeeAttendanceMachinesToSave);
//
//        // Map sang Response DTO và trả về
//        return savedEmployeeAttendanceMachines.stream()
//                .map(employeeAttendanceMachineMapper::toEmployeeAttendanceMachineResponse)
//                .toList();
//    }
//
//    /**
//     * Xử lý Bulk Delete
//     */
//    @Transactional
//    public void bulkDeleteEmployeeAttendanceMachines(List<Long> ids) {
//        // Kiểm tra xem có bao nhiêu ID tồn tại
//        long existingCount = employeeAttendanceMachineRepository.countByEmployeeAttendanceMachineIdIn(ids);
//        if (existingCount != ids.size()) {
//            // Không phải tất cả ID đều tồn tại
//            throw new NotFoundException("Some" + entityName + "s not found. Cannot complete bulk delete.");
//        }
//
//        // Xóa tất cả bằng ID trong 1 câu query (hiệu quả)
//        employeeAttendanceMachineRepository.deleteAllById(ids);
//    }


    public List<EmployeeAttendanceMachineResponse> getEmployeeAttendanceMachines(Pageable pageable) {
        Page<EmployeeAttendanceMachine> page = employeeAttendanceMachineRepository.findAll(pageable);
        return page.getContent()
                .stream().map(employeeAttendanceMachineMapper::toEmployeeAttendanceMachineResponse).toList();
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

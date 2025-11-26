package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.AttendanceMachine.AttendanceMachineRequest;
import com.example.demo.dto.humanresource.AttendanceMachine.AttendanceMachineResponse;
import com.example.demo.entity.humanresource.AttendanceMachine;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.AttendanceMachineMapper;
import com.example.demo.repository.humanresource.AttendanceMachineRepository;
import com.example.demo.repository.humanresource.EmployeeAttendanceMachineRepository;
import com.example.demo.exception.CannotDeleteException;
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
public class AttendanceMachineService {
    final AttendanceMachineRepository attendanceMachineRepository;
    final AttendanceMachineMapper attendanceMachineMapper;
    final EmployeeAttendanceMachineRepository employeeAttendanceMachineRepository;

    @Value("${entities.humanresource.attendancemachine}")
    private String entityName;


    public AttendanceMachineResponse createAttendanceMachine(AttendanceMachineRequest request) {
        // Check source_id uniqueness for create
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            attendanceMachineRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        AttendanceMachine attendanceMachine = attendanceMachineMapper.toAttendanceMachine(request);

        return attendanceMachineMapper.toAttendanceMachineResponse(attendanceMachineRepository.save(attendanceMachine));
    }


//    /**
//     * Xử lý Bulk Upsert
//     */
//    @Transactional
//    public List<AttendanceMachineResponse> bulkUpsertAttendanceMachines(List<AttendanceMachineRequest> requests) {
//
//        // Lấy tất cả attendanceMachineCodes từ request
//        List<String> attendanceMachineCodes = requests.stream()
//                .map(AttendanceMachineRequest::getAttendanceMachineCode)
//                .toList();
//
//        // Tìm tất cả các attendanceMachine đã tồn tại TRONG 1 CÂU QUERY
//        Map<String, AttendanceMachine> existingAttendanceMachinesMap = attendanceMachineRepository.findByAttendanceMachineCodeIn(attendanceMachineCodes).stream()
//                .collect(Collectors.toMap(AttendanceMachine::getAttendanceMachineCode, attendanceMachine -> attendanceMachine));
//
//        List<AttendanceMachine> attendanceMachinesToSave = new java.util.ArrayList<>();
//
//        // Lặp qua danh sách request để quyết định UPDATE hay INSERT
//        for (AttendanceMachineRequest request : requests) {
//            AttendanceMachine attendanceMachine = existingAttendanceMachinesMap.get(request.getAttendanceMachineCode());
//
//            if (attendanceMachine != null) {
//                // --- Logic UPDATE ---
//                // AttendanceMachine đã tồn tại -> Cập nhật
//                attendanceMachineMapper.updateAttendanceMachine(attendanceMachine, request);
//                attendanceMachinesToSave.add(attendanceMachine);
//            } else {
//                // --- Logic INSERT ---
//                // AttendanceMachine chưa tồn tại -> Tạo mới
//                AttendanceMachine newAttendanceMachine = attendanceMachineMapper.toAttendanceMachine(request);
//                attendanceMachinesToSave.add(newAttendanceMachine);
//            }
//        }
//
//        // Lưu tất cả (cả insert và update) TRONG 1 LỆNH
//        List<AttendanceMachine> savedAttendanceMachines = attendanceMachineRepository.saveAll(attendanceMachinesToSave);
//
//        // Map sang Response DTO và trả về
//        return savedAttendanceMachines.stream()
//                .map(attendanceMachineMapper::toAttendanceMachineResponse)
//                .toList();
//    }
//
//    /**
//     * Xử lý Bulk Delete
//     */
//    @Transactional
//    public void bulkDeleteAttendanceMachines(List<Long> ids) {
//        // Kiểm tra xem có bao nhiêu ID tồn tại
//        long existingCount = attendanceMachineRepository.countByAttendanceMachineIdIn(ids);
//        if (existingCount != ids.size()) {
//            // Không phải tất cả ID đều tồn tại
//            throw new NotFoundException("Some" + entityName + "s not found. Cannot complete bulk delete.");
//        }
//
//        // Xóa tất cả bằng ID trong 1 câu query (hiệu quả)
//        attendanceMachineRepository.deleteAllById(ids);
//    }

    public List<AttendanceMachineResponse> getAttendanceMachines(Pageable pageable) {
        Page<AttendanceMachine> page = attendanceMachineRepository.findAll(pageable);
        return page.getContent()
                .stream().map(attendanceMachineMapper::toAttendanceMachineResponse).toList();
    }

    public AttendanceMachineResponse getAttendanceMachine(Long id) {
        return attendanceMachineMapper.toAttendanceMachineResponse(attendanceMachineRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public AttendanceMachineResponse updateAttendanceMachine(Long id, AttendanceMachineRequest request) {
        AttendanceMachine attendanceMachine = attendanceMachineRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            attendanceMachineRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getAttendanceMachineId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        attendanceMachineMapper.updateAttendanceMachine(attendanceMachine, request);

        return attendanceMachineMapper.toAttendanceMachineResponse(attendanceMachineRepository.save(attendanceMachine));
    }

    public void deleteAttendanceMachine(Long id) {
        if (!attendanceMachineRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        // Check references (RESTRICT strategy)
        long refCount = employeeAttendanceMachineRepository.countByMachine_AttendanceMachineId(id);
        if (refCount > 0) {
            throw new CannotDeleteException(
                    "AttendanceMachine", id, "EmployeeAttendanceMachine", refCount
            );
        }

        attendanceMachineRepository.deleteById(id);
    }
}

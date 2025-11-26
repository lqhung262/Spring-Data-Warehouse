package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.WorkShift.WorkShiftRequest;
import com.example.demo.dto.humanresource.WorkShift.WorkShiftResponse;
import com.example.demo.entity.humanresource.WorkShift;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.WorkShiftMapper;
import com.example.demo.exception.CannotDeleteException;
import com.example.demo.repository.humanresource.WorkShiftRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.demo.repository.humanresource.EmployeeWorkShiftRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WorkShiftService {
    final WorkShiftRepository workShiftRepository;
    final WorkShiftMapper workShiftMapper;
    final EmployeeWorkShiftRepository employeeWorkShiftRepository;

    @Value("${entities.humanresource.workshift}")
    private String entityName;

    public WorkShiftResponse createWorkShift(WorkShiftRequest request) {
        // Check source_id uniqueness for create
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            workShiftRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        WorkShift workShift = workShiftMapper.toWorkShift(request);

        return workShiftMapper.toWorkShiftResponse(workShiftRepository.save(workShift));
    }

    /**
     * Xử lý Bulk Upsert
     */
//    @Transactional
//    public List<WorkShiftResponse> bulkUpsertWorkShifts(List<WorkShiftRequest> requests) {
//
//        // Lấy tất cả workShiftCodes từ request
//        List<String> workShiftCodes = requests.stream()
//                .map(WorkShiftRequest::getWorkShiftCode)
//                .toList();
//
//        // Tìm tất cả các workShift đã tồn tại TRONG 1 CÂU QUERY
//        Map<String, WorkShift> existingWorkShiftsMap = workShiftRepository.findByWorkShiftCodeIn(workShiftCodes).stream()
//                .collect(Collectors.toMap(WorkShift::getWorkShiftCode, workShift -> workShift));
//
//        List<WorkShift> workShiftsToSave = new java.util.ArrayList<>();
//
//        // Lặp qua danh sách request để quyết định UPDATE hay INSERT
//        for (WorkShiftRequest request : requests) {
//            WorkShift workShift = existingWorkShiftsMap.get(request.getWorkShiftCode());
//
//            if (workShift != null) {
//                // --- Logic UPDATE ---
//                // WorkShift đã tồn tại -> Cập nhật
//                workShiftMapper.updateWorkShift(workShift, request);
//                workShiftsToSave.add(workShift);
//            } else {
//                // --- Logic INSERT ---
//                // WorkShift chưa tồn tại -> Tạo mới
//                WorkShift newWorkShift = workShiftMapper.toWorkShift(request);
//                workShiftsToSave.add(newWorkShift);
//            }
//        }
//
//        // Lưu tất cả (cả insert và update) TRONG 1 LỆNH
//        List<WorkShift> savedWorkShifts = workShiftRepository.saveAll(workShiftsToSave);
//
//        // Map sang Response DTO và trả về
//        return savedWorkShifts.stream()
//                .map(workShiftMapper::toWorkShiftResponse)
//                .toList();
//    }
//
//    /**
//     * Xử lý Bulk Delete
//     */
//    @Transactional
//    public void bulkDeleteWorkShifts(List<Long> ids) {
//        // Kiểm tra xem có bao nhiêu ID tồn tại
//        long existingCount = workShiftRepository.countByWorkShiftIdIn(ids);
//        if (existingCount != ids.size()) {
//            // Không phải tất cả ID đều tồn tại
//            throw new NotFoundException("Some" + entityName + "s not found. Cannot complete bulk delete.");
//        }
//
//        // Xóa tất cả bằng ID trong 1 câu query (hiệu quả)
//        workShiftRepository.deleteAllById(ids);
//    }
    public List<WorkShiftResponse> getWorkShifts(Pageable pageable) {
        Page<WorkShift> page = workShiftRepository.findAll(pageable);
        return page.getContent()
                .stream().map(workShiftMapper::toWorkShiftResponse).toList();
    }

    public WorkShiftResponse getWorkShift(Long id) {
        return workShiftMapper.toWorkShiftResponse(workShiftRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public WorkShiftResponse updateWorkShift(Long id, WorkShiftRequest request) {
        WorkShift workShift = workShiftRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            workShiftRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getWorkShiftId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        workShiftMapper.updateWorkShift(workShift, request);

        return workShiftMapper.toWorkShiftResponse(workShiftRepository.save(workShift));
    }

    public void deleteWorkShift(Long id) {
        if (!workShiftRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        // Check references (RESTRICT strategy)
        long refCount = employeeWorkShiftRepository.countByWorkShift_WorkShiftId(id);
        if (refCount > 0) {
            throw new CannotDeleteException(
                    "WorkShift", id, "EmployeeWorkShift", refCount
            );
        }

        workShiftRepository.deleteById(id);
    }
}

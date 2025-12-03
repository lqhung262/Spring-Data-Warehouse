package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.WorkShiftGroup.WorkShiftGroupRequest;
import com.example.demo.dto.humanresource.WorkShiftGroup.WorkShiftGroupResponse;
import com.example.demo.entity.humanresource.WorkShiftGroup;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.WorkShiftGroupMapper;
import com.example.demo.repository.humanresource.WorkShiftGroupRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.demo.repository.humanresource.EmployeeWorkShiftRepository;
import com.example.demo.exception.CannotDeleteException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WorkShiftGroupService {
    final WorkShiftGroupRepository workShiftGroupRepository;
    final WorkShiftGroupMapper workShiftGroupMapper;
    final EmployeeWorkShiftRepository employeeWorkShiftRepository;

    @Value("${entities.humanresource.workshiftgroup}")
    private String entityName;

    public WorkShiftGroupResponse createWorkShiftGroup(WorkShiftGroupRequest request) {
        // Check source_id uniqueness for create
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            workShiftGroupRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        WorkShiftGroup workShiftGroup = workShiftGroupMapper.toWorkShiftGroup(request);

        return workShiftGroupMapper.toWorkShiftGroupResponse(workShiftGroupRepository.save(workShiftGroup));
    }

    /**
     * Xử lý Bulk Upsert
     */
//    @Transactional
//    public List<WorkShiftGroupResponse> bulkUpsertWorkShiftGroups(List<WorkShiftGroupRequest> requests) {
//
//        // Lấy tất cả workShiftGroupCodes từ request
//        List<String> workShiftGroupCodes = requests.stream()
//                .map(WorkShiftGroupRequest::getWorkShiftGroupCode)
//                .toList();
//
//        // Tìm tất cả các workShiftGroup đã tồn tại TRONG 1 CÂU QUERY
//        Map<String, WorkShiftGroup> existingWorkShiftGroupsMap = workShiftGroupRepository.findByWorkShiftGroupCodeIn(workShiftGroupCodes).stream()
//                .collect(Collectors.toMap(WorkShiftGroup::getWorkShiftGroupCode, workShiftGroup -> workShiftGroup));
//
//        List<WorkShiftGroup> workShiftGroupsToSave = new java.util.ArrayList<>();
//
//        // Lặp qua danh sách request để quyết định UPDATE hay INSERT
//        for (WorkShiftGroupRequest request : requests) {
//            WorkShiftGroup workShiftGroup = existingWorkShiftGroupsMap.get(request.getWorkShiftGroupCode());
//
//            if (workShiftGroup != null) {
//                // --- Logic UPDATE ---
//                // WorkShiftGroup đã tồn tại -> Cập nhật
//                workShiftGroupMapper.updateWorkShiftGroup(workShiftGroup, request);
//                workShiftGroupsToSave.add(workShiftGroup);
//            } else {
//                // --- Logic INSERT ---
//                // WorkShiftGroup chưa tồn tại -> Tạo mới
//                WorkShiftGroup newWorkShiftGroup = workShiftGroupMapper.toWorkShiftGroup(request);
//                workShiftGroupsToSave.add(newWorkShiftGroup);
//            }
//        }
//
//        // Lưu tất cả (cả insert và update) TRONG 1 LỆNH
//        List<WorkShiftGroup> savedWorkShiftGroups = workShiftGroupRepository.saveAll(workShiftGroupsToSave);
//
//        // Map sang Response DTO và trả về
//        return savedWorkShiftGroups.stream()
//                .map(workShiftGroupMapper::toWorkShiftGroupResponse)
//                .toList();
//    }
//
//    /**
//     * Xử lý Bulk Delete
//     */
//    @Transactional
//    public void bulkDeleteWorkShiftGroups(List<Long> ids) {
//        // Kiểm tra xem có bao nhiêu ID tồn tại
//        long existingCount = workShiftGroupRepository.countByWorkShiftGroupIdIn(ids);
//        if (existingCount != ids.size()) {
//            // Không phải tất cả ID đều tồn tại
//            throw new NotFoundException("Some" + entityName + "s not found. Cannot complete bulk delete.");
//        }
//
//        // Xóa tất cả bằng ID trong 1 câu query (hiệu quả)
//        workShiftGroupRepository.deleteAllById(ids);
//    }
    public List<WorkShiftGroupResponse> getWorkShiftGroups(Pageable pageable) {
        Page<WorkShiftGroup> page = workShiftGroupRepository.findAll(pageable);
        return page.getContent()
                .stream().map(workShiftGroupMapper::toWorkShiftGroupResponse).toList();
    }

    public WorkShiftGroupResponse getWorkShiftGroup(Long id) {
        return workShiftGroupMapper.toWorkShiftGroupResponse(workShiftGroupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public WorkShiftGroupResponse updateWorkShiftGroup(Long id, WorkShiftGroupRequest request) {
        WorkShiftGroup workShiftGroup = workShiftGroupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            workShiftGroupRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getWorkShiftGroupId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        workShiftGroupMapper.updateWorkShiftGroup(workShiftGroup, request);

        return workShiftGroupMapper.toWorkShiftGroupResponse(workShiftGroupRepository.save(workShiftGroup));
    }

    public void deleteWorkShiftGroup(Long id) {
        checkForeignKeyConstraints(id);

        workShiftGroupRepository.deleteById(id);
    }

    private void checkForeignKeyConstraints(Long id) {
        if (!workShiftGroupRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        // Check references (RESTRICT strategy)
        long refCount = employeeWorkShiftRepository.countByWorkShiftGroup_WorkShiftGroupId(id);
        if (refCount > 0) {
            throw new CannotDeleteException(
                    "WorkShiftGroup", id, "EmployeeWorkShift", refCount
            );
        }
    }
}

package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.WorkLocation.WorkLocationRequest;
import com.example.demo.dto.humanresource.WorkLocation.WorkLocationResponse;
import com.example.demo.entity.humanresource.WorkLocation;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.WorkLocationMapper;
import com.example.demo.repository.humanresource.WorkLocationRepository;
import com.example.demo.repository.humanresource.EmployeeWorkLocationRepository;
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

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WorkLocationService {
    final WorkLocationRepository workLocationRepository;
    final WorkLocationMapper workLocationMapper;
    final EmployeeWorkLocationRepository employeeWorkLocationRepository;

    @Value("${entities.humanresource.worklocation}")
    private String entityName;


    public WorkLocationResponse createWorkLocation(WorkLocationRequest request) {
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            workLocationRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        WorkLocation workLocation = workLocationMapper.toWorkLocation(request);

        return workLocationMapper.toWorkLocationResponse(workLocationRepository.save(workLocation));
    }

    /**
     * Xử lý Bulk Upsert
     */
//    @Transactional
//    public List<WorkLocationResponse> bulkUpsertWorkLocations(List<WorkLocationRequest> requests) {
//
//        // Lấy tất cả workLocationCodes từ request
//        List<String> workLocationCodes = requests.stream()
//                .map(WorkLocationRequest::getWorkLocationCode)
//                .toList();
//
//        // Tìm tất cả các workLocation đã tồn tại TRONG 1 CÂU QUERY
//        Map<String, WorkLocation> existingWorkLocationsMap = workLocationRepository.findByWorkLocationCodeIn(workLocationCodes).stream()
//                .collect(Collectors.toMap(WorkLocation::getWorkLocationCode, workLocation -> workLocation));
//
//        List<WorkLocation> workLocationsToSave = new java.util.ArrayList<>();
//
//        // Lặp qua danh sách request để quyết định UPDATE hay INSERT
//        for (WorkLocationRequest request : requests) {
//            WorkLocation workLocation = existingWorkLocationsMap.get(request.getWorkLocationCode());
//
//            if (workLocation != null) {
//                // --- Logic UPDATE ---
//                // WorkLocation đã tồn tại -> Cập nhật
//                workLocationMapper.updateWorkLocation(workLocation, request);
//                workLocationsToSave.add(workLocation);
//            } else {
//                // --- Logic INSERT ---
//                // WorkLocation chưa tồn tại -> Tạo mới
//                WorkLocation newWorkLocation = workLocationMapper.toWorkLocation(request);
//                workLocationsToSave.add(newWorkLocation);
//            }
//        }
//
//        // Lưu tất cả (cả insert và update) TRONG 1 LỆNH
//        List<WorkLocation> savedWorkLocations = workLocationRepository.saveAll(workLocationsToSave);
//
//        // Map sang Response DTO và trả về
//        return savedWorkLocations.stream()
//                .map(workLocationMapper::toWorkLocationResponse)
//                .toList();
//    }
//
//    /**
//     * Xử lý Bulk Delete
//     */
//    @Transactional
//    public void bulkDeleteWorkLocations(List<Long> ids) {
//        // Kiểm tra xem có bao nhiêu ID tồn tại
//        long existingCount = workLocationRepository.countByWorkLocationIdIn(ids);
//        if (existingCount != ids.size()) {
//            // Không phải tất cả ID đều tồn tại
//            throw new NotFoundException("Some" + entityName + "s not found. Cannot complete bulk delete.");
//        }
//
//        // Xóa tất cả bằng ID trong 1 câu query (hiệu quả)
//        workLocationRepository.deleteAllById(ids);
//    }
    public List<WorkLocationResponse> getWorkLocations(Pageable pageable) {
        Page<WorkLocation> page = workLocationRepository.findAll(pageable);
        return page.getContent()
                .stream().map(workLocationMapper::toWorkLocationResponse).toList();
    }

    public WorkLocationResponse getWorkLocation(Long id) {
        return workLocationMapper.toWorkLocationResponse(workLocationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public WorkLocationResponse updateWorkLocation(Long id, WorkLocationRequest request) {
        WorkLocation workLocation = workLocationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            workLocationRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getWorkLocationId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        workLocationMapper.updateWorkLocation(workLocation, request);

        return workLocationMapper.toWorkLocationResponse(workLocationRepository.save(workLocation));
    }

    public void deleteWorkLocation(Long id) {
        if (!workLocationRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        // Check references (RESTRICT strategy)
        long refCount = employeeWorkLocationRepository.countByWorkLocation_WorkLocationId(id);
        if (refCount > 0) {
            throw new CannotDeleteException(
                    "WorkLocation", id, "EmployeeWorkLocation", refCount
            );
        }

        workLocationRepository.deleteById(id);
    }
}

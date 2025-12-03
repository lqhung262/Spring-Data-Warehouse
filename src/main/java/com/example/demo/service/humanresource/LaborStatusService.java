package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.LaborStatus.LaborStatusRequest;
import com.example.demo.dto.humanresource.LaborStatus.LaborStatusResponse;
import com.example.demo.entity.humanresource.LaborStatus;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.LaborStatusMapper;
import com.example.demo.repository.humanresource.LaborStatusRepository;
import com.example.demo.repository.humanresource.EmployeeRepository;
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
public class LaborStatusService {
    final LaborStatusRepository laborStatusRepository;
    final LaborStatusMapper laborStatusMapper;
    final EmployeeRepository employeeRepository;

    @Value("${entities.humanresource.laborstatus}")
    private String entityName;

    public LaborStatusResponse createLaborStatus(LaborStatusRequest request) {
        // Check source_id uniqueness for create
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            laborStatusRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        LaborStatus laborStatus = laborStatusMapper.toLaborStatus(request);

        return laborStatusMapper.toLaborStatusResponse(laborStatusRepository.save(laborStatus));
    }

    /**
     * Xử lý Bulk Upsert
     */
//    @Transactional
//    public List<LaborStatusResponse> bulkUpsertLaborStatuses(List<LaborStatusRequest> requests) {
//
//        // Lấy tất cả laborStatusCodes từ request
//        List<String> laborStatusCodes = requests.stream()
//                .map(LaborStatusRequest::getLaborStatusCode)
//                .toList();
//
//        // Tìm tất cả các laborStatus đã tồn tại TRONG 1 CÂU QUERY
//        Map<String, LaborStatus> existingLaborStatusesMap = laborStatusRepository.findByLaborStatusCodeIn(laborStatusCodes).stream()
//                .collect(Collectors.toMap(LaborStatus::getLaborStatusCode, laborStatus -> laborStatus));
//
//        List<LaborStatus> laborStatusesToSave = new java.util.ArrayList<>();
//
//        // Lặp qua danh sách request để quyết định UPDATE hay INSERT
//        for (LaborStatusRequest request : requests) {
//            LaborStatus laborStatus = existingLaborStatusesMap.get(request.getLaborStatusCode());
//
//            if (laborStatus != null) {
//                // --- Logic UPDATE ---
//                // LaborStatus đã tồn tại -> Cập nhật
//                laborStatusMapper.updateLaborStatus(laborStatus, request);
//                laborStatusesToSave.add(laborStatus);
//            } else {
//                // --- Logic INSERT ---
//                // LaborStatus chưa tồn tại -> Tạo mới
//                LaborStatus newLaborStatus = laborStatusMapper.toLaborStatus(request);
//                laborStatusesToSave.add(newLaborStatus);
//            }
//        }
//
//        // Lưu tất cả (cả insert và update) TRONG 1 LỆNH
//        List<LaborStatus> savedLaborStatuses = laborStatusRepository.saveAll(laborStatusesToSave);
//
//        // Map sang Response DTO và trả về
//        return savedLaborStatuses.stream()
//                .map(laborStatusMapper::toLaborStatusResponse)
//                .toList();
//    }
//
//    /**
//     * Xử lý Bulk Delete
//     */
//    @Transactional
//    public void bulkDeleteLaborStatuses(List<Long> ids) {
//        // Kiểm tra xem có bao nhiêu ID tồn tại
//        long existingCount = laborStatusRepository.countByLaborStatusIdIn(ids);
//        if (existingCount != ids.size()) {
//            // Không phải tất cả ID đều tồn tại
//            throw new NotFoundException("Some" + entityName + "s not found. Cannot complete bulk delete.");
//        }
//
//        // Xóa tất cả bằng ID trong 1 câu query (hiệu quả)
//        laborStatusRepository.deleteAllById(ids);
//    }
    public List<LaborStatusResponse> getLaborStatuses(Pageable pageable) {
        Page<LaborStatus> page = laborStatusRepository.findAll(pageable);
        return page.getContent()
                .stream().map(laborStatusMapper::toLaborStatusResponse).toList();
    }

    public LaborStatusResponse getLaborStatus(Long id) {
        return laborStatusMapper.toLaborStatusResponse(laborStatusRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public LaborStatusResponse updateLaborStatus(Long id, LaborStatusRequest request) {
        LaborStatus laborStatus = laborStatusRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            laborStatusRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getLaborStatusId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        laborStatusMapper.updateLaborStatus(laborStatus, request);

        return laborStatusMapper.toLaborStatusResponse(laborStatusRepository.save(laborStatus));
    }

    public void deleteLaborStatus(Long id) {
        checkReferenceBeforeDelete(id);

        laborStatusRepository.deleteById(id);
    }

    private void checkReferenceBeforeDelete(Long id) {
        if (!laborStatusRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        // Check references (RESTRICT strategy)
        long refCount = employeeRepository.countByLaborStatus_LaborStatusId(id);
        if (refCount > 0) {
            throw new CannotDeleteException(
                    "LaborStatus", id, "Employee", refCount
            );
        }
    }
}

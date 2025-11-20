package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.MaritalStatus.MaritalStatusRequest;
import com.example.demo.dto.humanresource.MaritalStatus.MaritalStatusResponse;
import com.example.demo.entity.humanresource.MaritalStatus;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.MaritalStatusMapper;
import com.example.demo.repository.humanresource.MaritalStatusRepository;
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
public class MaritalStatusService {
    final MaritalStatusRepository maritalStatusRepository;
    final MaritalStatusMapper maritalStatusMapper;

    @Value("${entities.humanresource.maritalstatus}")
    private String entityName;


    public MaritalStatusResponse createMaritalStatus(MaritalStatusRequest request) {
        // Check source_id uniqueness for create
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            maritalStatusRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        MaritalStatus maritalStatus = maritalStatusMapper.toMaritalStatus(request);

        return maritalStatusMapper.toMaritalStatusResponse(maritalStatusRepository.save(maritalStatus));
    }


    /**
     * Xử lý Bulk Upsert
     */
//    @Transactional
//    public List<MaritalStatusResponse> bulkUpsertMaritalStatuses(List<MaritalStatusRequest> requests) {
//
//        // Lấy tất cả maritalStatusCodes từ request
//        List<String> maritalStatusCodes = requests.stream()
//                .map(MaritalStatusRequest::getMaritalStatusCode)
//                .toList();
//
//        // Tìm tất cả các maritalStatus đã tồn tại TRONG 1 CÂU QUERY
//        Map<String, MaritalStatus> existingMaritalStatusesMap = maritalStatusRepository.findByMaritalStatusCodeIn(maritalStatusCodes).stream()
//                .collect(Collectors.toMap(MaritalStatus::getMaritalStatusCode, maritalStatus -> maritalStatus));
//
//        List<MaritalStatus> maritalStatusesToSave = new java.util.ArrayList<>();
//
//        // Lặp qua danh sách request để quyết định UPDATE hay INSERT
//        for (MaritalStatusRequest request : requests) {
//            MaritalStatus maritalStatus = existingMaritalStatusesMap.get(request.getMaritalStatusCode());
//
//            if (maritalStatus != null) {
//                // --- Logic UPDATE ---
//                // MaritalStatus đã tồn tại -> Cập nhật
//                maritalStatusMapper.updateMaritalStatus(maritalStatus, request);
//                maritalStatusesToSave.add(maritalStatus);
//            } else {
//                // --- Logic INSERT ---
//                // MaritalStatus chưa tồn tại -> Tạo mới
//                MaritalStatus newMaritalStatus = maritalStatusMapper.toMaritalStatus(request);
//                maritalStatusesToSave.add(newMaritalStatus);
//            }
//        }
//
//        // Lưu tất cả (cả insert và update) TRONG 1 LỆNH
//        List<MaritalStatus> savedMaritalStatuses = maritalStatusRepository.saveAll(maritalStatusesToSave);
//
//        // Map sang Response DTO và trả về
//        return savedMaritalStatuses.stream()
//                .map(maritalStatusMapper::toMaritalStatusResponse)
//                .toList();
//    }
//
//    /**
//     * Xử lý Bulk Delete
//     */
//    @Transactional
//    public void bulkDeleteMaritalStatuses(List<Long> ids) {
//        // Kiểm tra xem có bao nhiêu ID tồn tại
//        long existingCount = maritalStatusRepository.countByMaritalStatusIdIn(ids);
//        if (existingCount != ids.size()) {
//            // Không phải tất cả ID đều tồn tại
//            throw new NotFoundException("Some" + entityName + "s not found. Cannot complete bulk delete.");
//        }
//
//        // Xóa tất cả bằng ID trong 1 câu query (hiệu quả)
//        maritalStatusRepository.deleteAllById(ids);
//    }
    public List<MaritalStatusResponse> getMaritalStatuses(Pageable pageable) {
        Page<MaritalStatus> page = maritalStatusRepository.findAll(pageable);
        return page.getContent()
                .stream().map(maritalStatusMapper::toMaritalStatusResponse).toList();
    }

    public MaritalStatusResponse getMaritalStatus(Long id) {
        return maritalStatusMapper.toMaritalStatusResponse(maritalStatusRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public MaritalStatusResponse updateMaritalStatus(Long id, MaritalStatusRequest request) {
        MaritalStatus maritalStatus = maritalStatusRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            maritalStatusRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getMaritalStatusId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        maritalStatusMapper.updateMaritalStatus(maritalStatus, request);

        return maritalStatusMapper.toMaritalStatusResponse(maritalStatusRepository.save(maritalStatus));
    }

    public void deleteMaritalStatus(Long id) {
        MaritalStatus maritalStatus = maritalStatusRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        maritalStatusRepository.deleteById(id);
    }
}

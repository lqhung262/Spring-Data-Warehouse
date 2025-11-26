package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.OldWard.OldWardRequest;
import com.example.demo.dto.humanresource.OldWard.OldWardResponse;
import com.example.demo.entity.humanresource.OldWard;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.OldWardMapper;
import com.example.demo.repository.humanresource.OldWardRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.demo.repository.humanresource.EmployeeRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OldWardService {
    final OldWardRepository oldWardRepository;
    final OldWardMapper oldWardMapper;
    final EmployeeRepository employeeRepository;

    @Value("${entities.humanresource.oldward}")
    private String entityName;

    public OldWardResponse createOldWard(OldWardRequest request) {
        // Check source_id uniqueness for create
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            oldWardRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        OldWard oldWard = oldWardMapper.toOldWard(request);
        // Set FK references from IDs in request
        oldWardMapper.setReferences(oldWard, request);

        return oldWardMapper.toOldWardResponse(oldWardRepository.save(oldWard));
    }


    /**
     * Xử lý Bulk Upsert
     */
//    @Transactional
//    public List<OldWardResponse> bulkUpsertOldWards(List<OldWardRequest> requests) {
//
//        // Lấy tất cả oldWardCodes từ request
//        List<String> oldWardCodes = requests.stream()
//                .map(OldWardRequest::getOldWardCode)
//                .toList();
//
//        // Tìm tất cả các oldWard đã tồn tại TRONG 1 CÂU QUERY
//        Map<String, OldWard> existingOldWardsMap = oldWardRepository.findByOldWardCodeIn(oldWardCodes).stream()
//                .collect(Collectors.toMap(OldWard::getOldWardCode, oldWard -> oldWard));
//
//        List<OldWard> oldWardsToSave = new java.util.ArrayList<>();
//
//        // Lặp qua danh sách request để quyết định UPDATE hay INSERT
//        for (OldWardRequest request : requests) {
//            OldWard oldWard = existingOldWardsMap.get(request.getOldWardCode());
//
//            if (oldWard != null) {
//                // --- Logic UPDATE ---
//                // OldWard đã tồn tại -> Cập nhật
//                oldWardMapper.updateOldWard(oldWard, request);
//                oldWardsToSave.add(oldWard);
//            } else {
//                // --- Logic INSERT ---
//                // OldWard chưa tồn tại -> Tạo mới
//                OldWard newOldWard = oldWardMapper.toOldWard(request);
//                oldWardsToSave.add(newOldWard);
//            }
//        }
//
//        // Lưu tất cả (cả insert và update) TRONG 1 LỆNH
//        List<OldWard> savedOldWards = oldWardRepository.saveAll(oldWardsToSave);
//
//        // Map sang Response DTO và trả về
//        return savedOldWards.stream()
//                .map(oldWardMapper::toOldWardResponse)
//                .toList();
//    }
//
//    /**
//     * Xử lý Bulk Delete
//     */
//    @Transactional
//    public void bulkDeleteOldWards(List<Long> ids) {
//        // Kiểm tra xem có bao nhiêu ID tồn tại
//        long existingCount = oldWardRepository.countByOldWardIdIn(ids);
//        if (existingCount != ids.size()) {
//            // Không phải tất cả ID đều tồn tại
//            throw new NotFoundException("Some" + entityName + "s not found. Cannot complete bulk delete.");
//        }
//
//        // Xóa tất cả bằng ID trong 1 câu query (hiệu quả)
//        oldWardRepository.deleteAllById(ids);
//    }
    public List<OldWardResponse> getOldWards(Pageable pageable) {
        Page<OldWard> page = oldWardRepository.findAll(pageable);
        return page.getContent()
                .stream().map(oldWardMapper::toOldWardResponse).toList();
    }

    public OldWardResponse getOldWard(Long id) {
        return oldWardMapper.toOldWardResponse(oldWardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public OldWardResponse updateOldWard(Long id, OldWardRequest request) {
        OldWard oldWard = oldWardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            oldWardRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getOldWardId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        oldWardMapper.updateOldWard(oldWard, request);
        // Set FK references from IDs in request
        oldWardMapper.setReferences(oldWard, request);

        return oldWardMapper.toOldWardResponse(oldWardRepository.save(oldWard));
    }

    public void deleteOldWard(Long id) {
        if (!oldWardRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        // Note: OldWard can be safely deleted as no direct FK references
        // Database will enforce constraints if needed

        oldWardRepository.deleteById(id);
    }
}

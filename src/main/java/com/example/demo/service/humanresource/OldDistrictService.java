package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.OldDistrict.OldDistrictRequest;
import com.example.demo.dto.humanresource.OldDistrict.OldDistrictResponse;
import com.example.demo.entity.humanresource.OldDistrict;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.OldDistrictMapper;
import com.example.demo.repository.humanresource.OldDistrictRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.demo.repository.humanresource.OldWardRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OldDistrictService {
    final OldDistrictRepository oldDistrictRepository;
    final OldDistrictMapper oldDistrictMapper;
    final OldWardRepository oldWardRepository;

    @Value("${entities.humanresource.olddistrict}")
    private String entityName;

    public OldDistrictResponse createOldDistrict(OldDistrictRequest request) {
        // Check source_id uniqueness for create
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            oldDistrictRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        OldDistrict oldDistrict = oldDistrictMapper.toOldDistrict(request);

        // Set FK references from IDs in request
        oldDistrictMapper.setReferences(oldDistrict, request);

        return oldDistrictMapper.toOldDistrictResponse(oldDistrictRepository.save(oldDistrict));
    }

    /**
     * Xử lý Bulk Upsert
     */
//    @Transactional
//    public List<OldDistrictResponse> bulkUpsertOldDistricts(List<OldDistrictRequest> requests) {
//
//        // Lấy tất cả oldDistrictCodes từ request
//        List<String> oldDistrictCodes = requests.stream()
//                .map(OldDistrictRequest::getOldDistrictCode)
//                .toList();
//
//        // Tìm tất cả các oldDistrict đã tồn tại TRONG 1 CÂU QUERY
//        Map<String, OldDistrict> existingOldDistrictsMap = oldDistrictRepository.findByOldDistrictCodeIn(oldDistrictCodes).stream()
//                .collect(Collectors.toMap(OldDistrict::getOldDistrictCode, oldDistrict -> oldDistrict));
//
//        List<OldDistrict> oldDistrictsToSave = new java.util.ArrayList<>();
//
//        // Lặp qua danh sách request để quyết định UPDATE hay INSERT
//        for (OldDistrictRequest request : requests) {
//            OldDistrict oldDistrict = existingOldDistrictsMap.get(request.getOldDistrictCode());
//
//            if (oldDistrict != null) {
//                // --- Logic UPDATE ---
//                // OldDistrict đã tồn tại -> Cập nhật
//                oldDistrictMapper.updateOldDistrict(oldDistrict, request);
//                oldDistrictsToSave.add(oldDistrict);
//            } else {
//                // --- Logic INSERT ---
//                // OldDistrict chưa tồn tại -> Tạo mới
//                OldDistrict newOldDistrict = oldDistrictMapper.toOldDistrict(request);
//                oldDistrictsToSave.add(newOldDistrict);
//            }
//        }
//
//        // Lưu tất cả (cả insert và update) TRONG 1 LỆNH
//        List<OldDistrict> savedOldDistricts = oldDistrictRepository.saveAll(oldDistrictsToSave);
//
//        // Map sang Response DTO và trả về
//        return savedOldDistricts.stream()
//                .map(oldDistrictMapper::toOldDistrictResponse)
//                .toList();
//    }
//
//    /**
//     * Xử lý Bulk Delete
//     */
//    @Transactional
//    public void bulkDeleteOldDistricts(List<Long> ids) {
//        // Kiểm tra xem có bao nhiêu ID tồn tại
//        long existingCount = oldDistrictRepository.countByOldDistrictIdIn(ids);
//        if (existingCount != ids.size()) {
//            // Không phải tất cả ID đều tồn tại
//            throw new NotFoundException("Some" + entityName + "s not found. Cannot complete bulk delete.");
//        }
//
//        // Xóa tất cả bằng ID trong 1 câu query (hiệu quả)
//        oldDistrictRepository.deleteAllById(ids);
//    }
    public List<OldDistrictResponse> getOldDistricts(Pageable pageable) {
        Page<OldDistrict> page = oldDistrictRepository.findAll(pageable);
        return page.getContent()
                .stream().map(oldDistrictMapper::toOldDistrictResponse).toList();
    }

    public OldDistrictResponse getOldDistrict(Long id) {
        return oldDistrictMapper.toOldDistrictResponse(oldDistrictRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public OldDistrictResponse updateOldDistrict(Long id, OldDistrictRequest request) {
        OldDistrict oldDistrict = oldDistrictRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            oldDistrictRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getOldDistrictId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        oldDistrictMapper.updateOldDistrict(oldDistrict, request);
        // Set FK references from IDs in request
        oldDistrictMapper.setReferences(oldDistrict, request);

        return oldDistrictMapper.toOldDistrictResponse(oldDistrictRepository.save(oldDistrict));
    }

    public void deleteOldDistrict(Long id) {
        checkForeignKeyConstraints(id);

        oldDistrictRepository.deleteById(id);
    }

    private void checkForeignKeyConstraints(Long id) {
        if (!oldDistrictRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        // Check if any OldWard references this OldDistrict (RESTRICT strategy)
        long childCount = oldWardRepository.countByOldDistrict_OldDistrictId(id);
        if (childCount > 0) {
            throw new com.example.demo.exception.CannotDeleteException(
                    "OldDistrict", id, "OldWard", childCount
            );
        }
    }
}

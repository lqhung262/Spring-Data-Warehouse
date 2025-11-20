package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.OtType.OtTypeRequest;
import com.example.demo.dto.humanresource.OtType.OtTypeResponse;
import com.example.demo.entity.humanresource.OtType;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.OtTypeMapper;
import com.example.demo.repository.humanresource.OtTypeRepository;
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
public class OtTypeService {
    final OtTypeRepository otTypeRepository;
    final OtTypeMapper otTypeMapper;

    @Value("${entities.humanresource.ottype}")
    private String entityName;

    public OtTypeResponse createOtType(OtTypeRequest request) {
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            otTypeRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        OtType otType = otTypeMapper.toOtType(request);

        return otTypeMapper.toOtTypeResponse(otTypeRepository.save(otType));
    }

    /**
     * Xử lý Bulk Upsert
     */
//    @Transactional
//    public List<OtTypeResponse> bulkUpsertOtTypes(List<OtTypeRequest> requests) {
//
//        // Lấy tất cả otTypeCodes từ request
//        List<String> otTypeCodes = requests.stream()
//                .map(OtTypeRequest::getOtTypeCode)
//                .toList();
//
//        // Tìm tất cả các otType đã tồn tại TRONG 1 CÂU QUERY
//        Map<String, OtType> existingOtTypesMap = otTypeRepository.findByOtTypeCodeIn(otTypeCodes).stream()
//                .collect(Collectors.toMap(OtType::getOtTypeCode, otType -> otType));
//
//        List<OtType> otTypesToSave = new java.util.ArrayList<>();
//
//        // Lặp qua danh sách request để quyết định UPDATE hay INSERT
//        for (OtTypeRequest request : requests) {
//            OtType otType = existingOtTypesMap.get(request.getOtTypeCode());
//
//            if (otType != null) {
//                // --- Logic UPDATE ---
//                // OtType đã tồn tại -> Cập nhật
//                otTypeMapper.updateOtType(otType, request);
//                otTypesToSave.add(otType);
//            } else {
//                // --- Logic INSERT ---
//                // OtType chưa tồn tại -> Tạo mới
//                OtType newOtType = otTypeMapper.toOtType(request);
//                otTypesToSave.add(newOtType);
//            }
//        }
//
//        // Lưu tất cả (cả insert và update) TRONG 1 LỆNH
//        List<OtType> savedOtTypes = otTypeRepository.saveAll(otTypesToSave);
//
//        // Map sang Response DTO và trả về
//        return savedOtTypes.stream()
//                .map(otTypeMapper::toOtTypeResponse)
//                .toList();
//    }
//
//    /**
//     * Xử lý Bulk Delete
//     */
//    @Transactional
//    public void bulkDeleteOtTypes(List<Long> ids) {
//        // Kiểm tra xem có bao nhiêu ID tồn tại
//        long existingCount = otTypeRepository.countByOtTypeIdIn(ids);
//        if (existingCount != ids.size()) {
//            // Không phải tất cả ID đều tồn tại
//            throw new NotFoundException("Some" + entityName + "s not found. Cannot complete bulk delete.");
//        }
//
//        // Xóa tất cả bằng ID trong 1 câu query (hiệu quả)
//        otTypeRepository.deleteAllById(ids);
//    }
    public List<OtTypeResponse> getOtTypes(Pageable pageable) {
        Page<OtType> page = otTypeRepository.findAll(pageable);
        return page.getContent()
                .stream().map(otTypeMapper::toOtTypeResponse).toList();
    }

    public OtTypeResponse getOtType(Long id) {
        return otTypeMapper.toOtTypeResponse(otTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public OtTypeResponse updateOtType(Long id, OtTypeRequest request) {
        OtType otType = otTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            otTypeRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getOtTypeId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        otTypeMapper.updateOtType(otType, request);

        return otTypeMapper.toOtTypeResponse(otTypeRepository.save(otType));
    }

    public void deleteOtType(Long id) {
        OtType otType = otTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        otTypeRepository.deleteById(id);
    }
}

package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.Ward.WardRequest;
import com.example.demo.dto.humanresource.Ward.WardResponse;
import com.example.demo.entity.humanresource.Ward;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.WardMapper;
import com.example.demo.repository.humanresource.WardRepository;
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
public class WardService {
    final WardRepository wardRepository;
    final WardMapper wardMapper;

    @Value("${entities.humanresource.ward}")
    private String entityName;

    public WardResponse createWard(WardRequest request) {
        // Check source_id uniqueness for create
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            wardRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        Ward ward = wardMapper.toWard(request);

        return wardMapper.toWardResponse(wardRepository.save(ward));
    }

    /**
     * Xử lý Bulk Upsert
     */
//    @Transactional
//    public List<WardResponse> bulkUpsertWards(List<WardRequest> requests) {
//
//        // Lấy tất cả wardCodes từ request
//        List<String> wardCodes = requests.stream()
//                .map(WardRequest::getWardCode)
//                .toList();
//
//        // Tìm tất cả các ward đã tồn tại TRONG 1 CÂU QUERY
//        Map<String, Ward> existingWardsMap = wardRepository.findByWardCodeIn(wardCodes).stream()
//                .collect(Collectors.toMap(Ward::getWardCode, ward -> ward));
//
//        List<Ward> wardsToSave = new java.util.ArrayList<>();
//
//        // Lặp qua danh sách request để quyết định UPDATE hay INSERT
//        for (WardRequest request : requests) {
//            Ward ward = existingWardsMap.get(request.getWardCode());
//
//            if (ward != null) {
//                // --- Logic UPDATE ---
//                // Ward đã tồn tại -> Cập nhật
//                wardMapper.updateWard(ward, request);
//                wardsToSave.add(ward);
//            } else {
//                // --- Logic INSERT ---
//                // Ward chưa tồn tại -> Tạo mới
//                Ward newWard = wardMapper.toWard(request);
//                wardsToSave.add(newWard);
//            }
//        }
//
//        // Lưu tất cả (cả insert và update) TRONG 1 LỆNH
//        List<Ward> savedWards = wardRepository.saveAll(wardsToSave);
//
//        // Map sang Response DTO và trả về
//        return savedWards.stream()
//                .map(wardMapper::toWardResponse)
//                .toList();
//    }
//
//    /**
//     * Xử lý Bulk Delete
//     */
//    @Transactional
//    public void bulkDeleteWards(List<Long> ids) {
//        // Kiểm tra xem có bao nhiêu ID tồn tại
//        long existingCount = wardRepository.countByWardIdIn(ids);
//        if (existingCount != ids.size()) {
//            // Không phải tất cả ID đều tồn tại
//            throw new NotFoundException("Some" + entityName + "s not found. Cannot complete bulk delete.");
//        }
//
//        // Xóa tất cả bằng ID trong 1 câu query (hiệu quả)
//        wardRepository.deleteAllById(ids);
//    }
    public List<WardResponse> getWards(Pageable pageable) {
        Page<Ward> page = wardRepository.findAll(pageable);
        return page.getContent()
                .stream().map(wardMapper::toWardResponse).toList();
    }

    public WardResponse getWard(Long id) {
        return wardMapper.toWardResponse(wardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public WardResponse updateWard(Long id, WardRequest request) {
        Ward ward = wardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            wardRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getWardId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        wardMapper.updateWard(ward, request);

        return wardMapper.toWardResponse(wardRepository.save(ward));
    }

    public void deleteWard(Long id) {
        Ward ward = wardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        wardRepository.deleteById(id);
    }
}

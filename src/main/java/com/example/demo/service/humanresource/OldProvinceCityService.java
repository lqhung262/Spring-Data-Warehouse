package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.OldProvinceCity.OldProvinceCityRequest;
import com.example.demo.dto.humanresource.OldProvinceCity.OldProvinceCityResponse;
import com.example.demo.entity.humanresource.OldProvinceCity;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.OldProvinceCityMapper;
import com.example.demo.repository.humanresource.OldProvinceCityRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.demo.repository.humanresource.OldDistrictRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OldProvinceCityService {
    final OldProvinceCityRepository oldProvinceCityRepository;
    final OldProvinceCityMapper oldProvinceCityMapper;
    final OldDistrictRepository oldDistrictRepository;

    @Value("${entities.humanresource.oldprovincecity}")
    private String entityName;


    public OldProvinceCityResponse createOldProvinceCity(OldProvinceCityRequest request) {
        // Check source_id uniqueness for create
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            oldProvinceCityRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        OldProvinceCity oldProvinceCity = oldProvinceCityMapper.toOldProvinceCity(request);

        // Set FK references from IDs in request
        oldProvinceCityMapper.setReferences(oldProvinceCity, request);

        return oldProvinceCityMapper.toOldProvinceCityResponse(oldProvinceCityRepository.save(oldProvinceCity));
    }


    /**
     * Xử lý Bulk Upsert
     */
//    @Transactional
//    public List<OldProvinceCityResponse> bulkUpsertOldProvinceCities(List<OldProvinceCityRequest> requests) {
//
//        // Lấy tất cả oldProvinceCityCodes từ request
//        List<String> oldProvinceCityCodes = requests.stream()
//                .map(OldProvinceCityRequest::getOldProvinceCityCode)
//                .toList();
//
//        // Tìm tất cả các oldProvinceCity đã tồn tại TRONG 1 CÂU QUERY
//        Map<String, OldProvinceCity> existingOldProvinceCitysMap = oldProvinceCityRepository.findByOldProvinceCityCodeIn(oldProvinceCityCodes).stream()
//                .collect(Collectors.toMap(OldProvinceCity::getOldProvinceCityCode, oldProvinceCity -> oldProvinceCity));
//
//        List<OldProvinceCity> oldProvinceCitysToSave = new java.util.ArrayList<>();
//
//        // Lặp qua danh sách request để quyết định UPDATE hay INSERT
//        for (OldProvinceCityRequest request : requests) {
//            OldProvinceCity oldProvinceCity = existingOldProvinceCitysMap.get(request.getOldProvinceCityCode());
//
//            if (oldProvinceCity != null) {
//                // --- Logic UPDATE ---
//                // OldProvinceCity đã tồn tại -> Cập nhật
//                oldProvinceCityMapper.updateOldProvinceCity(oldProvinceCity, request);
//                oldProvinceCitysToSave.add(oldProvinceCity);
//            } else {
//                // --- Logic INSERT ---
//                // OldProvinceCity chưa tồn tại -> Tạo mới
//                OldProvinceCity newOldProvinceCity = oldProvinceCityMapper.toOldProvinceCity(request);
//                oldProvinceCitysToSave.add(newOldProvinceCity);
//            }
//        }
//
//        // Lưu tất cả (cả insert và update) TRONG 1 LỆNH
//        List<OldProvinceCity> savedOldProvinceCitys = oldProvinceCityRepository.saveAll(oldProvinceCitysToSave);
//
//        // Map sang Response DTO và trả về
//        return savedOldProvinceCitys.stream()
//                .map(oldProvinceCityMapper::toOldProvinceCityResponse)
//                .toList();
//    }
//
//    /**
//     * Xử lý Bulk Delete
//     */
//    @Transactional
//    public void bulkDeleteOldProvinceCities(List<Long> ids) {
//        // Kiểm tra xem có bao nhiêu ID tồn tại
//        long existingCount = oldProvinceCityRepository.countByOldProvinceCityIdIn(ids);
//        if (existingCount != ids.size()) {
//            // Không phải tất cả ID đều tồn tại
//            throw new NotFoundException("Some" + entityName + "s not found. Cannot complete bulk delete.");
//        }
//
//        // Xóa tất cả bằng ID trong 1 câu query (hiệu quả)
//        oldProvinceCityRepository.deleteAllById(ids);
//    }
    public List<OldProvinceCityResponse> getOldProvinceCities(Pageable pageable) {
        Page<OldProvinceCity> page = oldProvinceCityRepository.findAll(pageable);
        return page.getContent()
                .stream().map(oldProvinceCityMapper::toOldProvinceCityResponse).toList();
    }

    public OldProvinceCityResponse getOldProvinceCity(Long id) {
        return oldProvinceCityMapper.toOldProvinceCityResponse(oldProvinceCityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public OldProvinceCityResponse updateOldProvinceCity(Long id, OldProvinceCityRequest request) {
        OldProvinceCity oldProvinceCity = oldProvinceCityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            oldProvinceCityRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getOldProvinceCityId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        oldProvinceCityMapper.updateOldProvinceCity(oldProvinceCity, request);
        // Set FK references from IDs in request
        oldProvinceCityMapper.setReferences(oldProvinceCity, request);

        return oldProvinceCityMapper.toOldProvinceCityResponse(oldProvinceCityRepository.save(oldProvinceCity));
    }

    public void deleteOldProvinceCity(Long id) {
        checkForeignKeyConstraints(id);

        oldProvinceCityRepository.deleteById(id);
    }

    private void checkForeignKeyConstraints(Long id) {
        if (!oldDistrictRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        // Check references (RESTRICT strategy)
        long childCount = oldDistrictRepository.countByOldProvinceCity_OldProvinceCityId(id);
        if (childCount > 0) {
            throw new com.example.demo.exception.CannotDeleteException(
                    "OldProvinceCity", id, "OldDistrict", childCount
            );
        }
    }
}

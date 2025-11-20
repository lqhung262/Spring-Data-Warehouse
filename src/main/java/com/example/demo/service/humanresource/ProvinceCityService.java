package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.ProvinceCity.ProvinceCityRequest;
import com.example.demo.dto.humanresource.ProvinceCity.ProvinceCityResponse;
import com.example.demo.entity.humanresource.ProvinceCity;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.ProvinceCityMapper;
import com.example.demo.repository.humanresource.ProvinceCityRepository;
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
public class ProvinceCityService {
    final ProvinceCityRepository provinceCityRepository;
    final ProvinceCityMapper provinceCityMapper;

    @Value("${entities.humanresource.provincecity}")
    private String entityName;

    public ProvinceCityResponse createProvinceCity(ProvinceCityRequest request) {
        // Check source_id uniqueness for create
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            provinceCityRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        ProvinceCity provinceCity = provinceCityMapper.toProvinceCity(request);

        return provinceCityMapper.toProvinceCityResponse(provinceCityRepository.save(provinceCity));
    }

    /**
     * Xử lý Bulk Upsert
     */
//    @Transactional
//    public List<ProvinceCityResponse> bulkUpsertProvinceCities(List<ProvinceCityRequest> requests) {
//
//        // Lấy tất cả provinceCityCodes từ request
//        List<String> provinceCityCodes = requests.stream()
//                .map(ProvinceCityRequest::getProvinceCityCode)
//                .toList();
//
//        // Tìm tất cả các provinceCity đã tồn tại TRONG 1 CÂU QUERY
//        Map<String, ProvinceCity> existingProvinceCitysMap = provinceCityRepository.findByProvinceCityCodeIn(provinceCityCodes).stream()
//                .collect(Collectors.toMap(ProvinceCity::getProvinceCityCode, provinceCity -> provinceCity));
//
//        List<ProvinceCity> provinceCitysToSave = new java.util.ArrayList<>();
//
//        // Lặp qua danh sách request để quyết định UPDATE hay INSERT
//        for (ProvinceCityRequest request : requests) {
//            ProvinceCity provinceCity = existingProvinceCitysMap.get(request.getProvinceCityCode());
//
//            if (provinceCity != null) {
//                // --- Logic UPDATE ---
//                // ProvinceCity đã tồn tại -> Cập nhật
//                provinceCityMapper.updateProvinceCity(provinceCity, request);
//                provinceCitysToSave.add(provinceCity);
//            } else {
//                // --- Logic INSERT ---
//                // ProvinceCity chưa tồn tại -> Tạo mới
//                ProvinceCity newProvinceCity = provinceCityMapper.toProvinceCity(request);
//                provinceCitysToSave.add(newProvinceCity);
//            }
//        }
//
//        // Lưu tất cả (cả insert và update) TRONG 1 LỆNH
//        List<ProvinceCity> savedProvinceCitys = provinceCityRepository.saveAll(provinceCitysToSave);
//
//        // Map sang Response DTO và trả về
//        return savedProvinceCitys.stream()
//                .map(provinceCityMapper::toProvinceCityResponse)
//                .toList();
//    }
//
//    /**
//     * Xử lý Bulk Delete
//     */
//    @Transactional
//    public void bulkDeleteProvinceCities(List<Long> ids) {
//        // Kiểm tra xem có bao nhiêu ID tồn tại
//        long existingCount = provinceCityRepository.countByProvinceCityIdIn(ids);
//        if (existingCount != ids.size()) {
//            // Không phải tất cả ID đều tồn tại
//            throw new NotFoundException("Some" + entityName + "s not found. Cannot complete bulk delete.");
//        }
//
//        // Xóa tất cả bằng ID trong 1 câu query (hiệu quả)
//        provinceCityRepository.deleteAllById(ids);
//    }
    public List<ProvinceCityResponse> getProvinceCities(Pageable pageable) {
        Page<ProvinceCity> page = provinceCityRepository.findAll(pageable);
        return page.getContent()
                .stream().map(provinceCityMapper::toProvinceCityResponse).toList();
    }

    public ProvinceCityResponse getProvinceCity(Long id) {
        return provinceCityMapper.toProvinceCityResponse(provinceCityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public ProvinceCityResponse updateProvinceCity(Long id, ProvinceCityRequest request) {
        ProvinceCity provinceCity = provinceCityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            provinceCityRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getProvinceCityId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        provinceCityMapper.updateProvinceCity(provinceCity, request);

        return provinceCityMapper.toProvinceCityResponse(provinceCityRepository.save(provinceCity));
    }

    public void deleteProvinceCity(Long id) {
        ProvinceCity provinceCity = provinceCityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        provinceCityRepository.deleteById(id);
    }
}

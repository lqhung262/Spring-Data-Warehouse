package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.MedicalFacility.MedicalFacilityRequest;
import com.example.demo.dto.humanresource.MedicalFacility.MedicalFacilityResponse;
import com.example.demo.entity.humanresource.MedicalFacility;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.MedicalFacilityMapper;
import com.example.demo.repository.humanresource.MedicalFacilityRepository;
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
public class MedicalFacilityService {
    final MedicalFacilityRepository medicalFacilityRepository;
    final MedicalFacilityMapper medicalFacilityMapper;

    @Value("${entities.humanresource.medicalfacility}")
    private String entityName;

    public MedicalFacilityResponse createMedicalFacility(MedicalFacilityRequest request) {
//        medicalFacilityRepository.findByMedicalFacilityCode(request.getMedicalFacilityCode()).ifPresent(b -> {
//            throw new IllegalArgumentException(entityName + " with medical Facility Code " + request.getMedicalFacilityCode() + " already exists.");
//        });

        MedicalFacility medicalFacility = medicalFacilityMapper.toMedicalFacility(request);

        return medicalFacilityMapper.toMedicalFacilityResponse(medicalFacilityRepository.save(medicalFacility));
    }


    /**
     * Xử lý Bulk Upsert
     */
//    @Transactional
//    public List<MedicalFacilityResponse> bulkUpsertMedicalFacilities(List<MedicalFacilityRequest> requests) {
//
//        // Lấy tất cả medicalFacilityCodes từ request
//        List<String> medicalFacilityCodes = requests.stream()
//                .map(MedicalFacilityRequest::getMedicalFacilityCode)
//                .toList();
//
//        // Tìm tất cả các medicalFacility đã tồn tại TRONG 1 CÂU QUERY
//        Map<String, MedicalFacility> existingMedicalFacilitiesMap = medicalFacilityRepository.findByMedicalFacilityCodeIn(medicalFacilityCodes).stream()
//                .collect(Collectors.toMap(MedicalFacility::getMedicalFacilityCode, medicalFacility -> medicalFacility));
//
//        List<MedicalFacility> medicalFacilitiesToSave = new java.util.ArrayList<>();
//
//        // Lặp qua danh sách request để quyết định UPDATE hay INSERT
//        for (MedicalFacilityRequest request : requests) {
//            MedicalFacility medicalFacility = existingMedicalFacilitiesMap.get(request.getMedicalFacilityCode());
//
//            if (medicalFacility != null) {
//                // --- Logic UPDATE ---
//                // MedicalFacility đã tồn tại -> Cập nhật
//                medicalFacilityMapper.updateMedicalFacility(medicalFacility, request);
//                medicalFacilitiesToSave.add(medicalFacility);
//            } else {
//                // --- Logic INSERT ---
//                // MedicalFacility chưa tồn tại -> Tạo mới
//                MedicalFacility newMedicalFacility = medicalFacilityMapper.toMedicalFacility(request);
//                medicalFacilitiesToSave.add(newMedicalFacility);
//            }
//        }
//
//        // Lưu tất cả (cả insert và update) TRONG 1 LỆNH
//        List<MedicalFacility> savedMedicalFacilities = medicalFacilityRepository.saveAll(medicalFacilitiesToSave);
//
//        // Map sang Response DTO và trả về
//        return savedMedicalFacilities.stream()
//                .map(medicalFacilityMapper::toMedicalFacilityResponse)
//                .toList();
//    }
//
//    /**
//     * Xử lý Bulk Delete
//     */
//    @Transactional
//    public void bulkDeleteMedicalFacilities(List<Long> ids) {
//        // Kiểm tra xem có bao nhiêu ID tồn tại
//        long existingCount = medicalFacilityRepository.countByMedicalFacilityIdIn(ids);
//        if (existingCount != ids.size()) {
//            // Không phải tất cả ID đều tồn tại
//            throw new NotFoundException("Some" + entityName + "s not found. Cannot complete bulk delete.");
//        }
//
//        // Xóa tất cả bằng ID trong 1 câu query (hiệu quả)
//        medicalFacilityRepository.deleteAllById(ids);
//    }
    public List<MedicalFacilityResponse> getMedicalFacilities(Pageable pageable) {
        Page<MedicalFacility> page = medicalFacilityRepository.findAll(pageable);
        return page.getContent()
                .stream().map(medicalFacilityMapper::toMedicalFacilityResponse).toList();
    }

    public MedicalFacilityResponse getMedicalFacility(Long id) {
        return medicalFacilityMapper.toMedicalFacilityResponse(medicalFacilityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public MedicalFacilityResponse updateMedicalFacility(Long id, MedicalFacilityRequest request) {
        MedicalFacility medicalFacility = medicalFacilityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        medicalFacilityMapper.updateMedicalFacility(medicalFacility, request);

        return medicalFacilityMapper.toMedicalFacilityResponse(medicalFacilityRepository.save(medicalFacility));
    }

    public void deleteMedicalFacility(Long id) {
        MedicalFacility medicalFacility = medicalFacilityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        medicalFacilityRepository.deleteById(id);
    }
}

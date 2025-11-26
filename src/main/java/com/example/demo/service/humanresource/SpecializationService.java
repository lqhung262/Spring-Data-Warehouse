package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.Specialization.SpecializationRequest;
import com.example.demo.dto.humanresource.Specialization.SpecializationResponse;
import com.example.demo.entity.humanresource.Specialization;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.SpecializationMapper;
import com.example.demo.repository.humanresource.SpecializationRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.demo.repository.humanresource.EmployeeEducationRepository;
import com.example.demo.exception.CannotDeleteException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpecializationService {
    final SpecializationRepository specializationRepository;
    final SpecializationMapper specializationMapper;
    final EmployeeEducationRepository employeeEducationRepository;

    @Value("${entities.humanresource.speicialization}")
    private String entityName;

    public SpecializationResponse createSpecialization(SpecializationRequest request) {
        // Check source_id uniqueness for create
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            specializationRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        Specialization specialization = specializationMapper.toSpecialization(request);

        return specializationMapper.toSpecializationResponse(specializationRepository.save(specialization));
    }

    /**
     * Xử lý Bulk Upsert
     */
//    @Transactional
//    public List<SpecializationResponse> bulkUpsertSpecializations(List<SpecializationRequest> requests) {
//
//        // Lấy tất cả specializationCodes từ request
//        List<String> specializationCodes = requests.stream()
//                .map(SpecializationRequest::getSpecializationCode)
//                .toList();
//
//        // Tìm tất cả các specialization đã tồn tại TRONG 1 CÂU QUERY
//        Map<String, Specialization> existingSpecializationsMap = specializationRepository.findBySpecializationCodeIn(specializationCodes).stream()
//                .collect(Collectors.toMap(Specialization::getSpecializationCode, specialization -> specialization));
//
//        List<Specialization> specializationsToSave = new java.util.ArrayList<>();
//
//        // Lặp qua danh sách request để quyết định UPDATE hay INSERT
//        for (SpecializationRequest request : requests) {
//            Specialization specialization = existingSpecializationsMap.get(request.getSpecializationCode());
//
//            if (specialization != null) {
//                // --- Logic UPDATE ---
//                // Specialization đã tồn tại -> Cập nhật
//                specializationMapper.updateSpecialization(specialization, request);
//                specializationsToSave.add(specialization);
//            } else {
//                // --- Logic INSERT ---
//                // Specialization chưa tồn tại -> Tạo mới
//                Specialization newSpecialization = specializationMapper.toSpecialization(request);
//                specializationsToSave.add(newSpecialization);
//            }
//        }
//
//        // Lưu tất cả (cả insert và update) TRONG 1 LỆNH
//        List<Specialization> savedSpecializations = specializationRepository.saveAll(specializationsToSave);
//
//        // Map sang Response DTO và trả về
//        return savedSpecializations.stream()
//                .map(specializationMapper::toSpecializationResponse)
//                .toList();
//    }
//
//    /**
//     * Xử lý Bulk Delete
//     */
//    @Transactional
//    public void bulkDeleteSpecializations(List<Long> ids) {
//        // Kiểm tra xem có bao nhiêu ID tồn tại
//        long existingCount = specializationRepository.countBySpecializationIdIn(ids);
//        if (existingCount != ids.size()) {
//            // Không phải tất cả ID đều tồn tại
//            throw new NotFoundException("Some" + entityName + "s not found. Cannot complete bulk delete.");
//        }
//
//        // Xóa tất cả bằng ID trong 1 câu query (hiệu quả)
//        specializationRepository.deleteAllById(ids);
//    }
    public List<SpecializationResponse> getSpecializations(Pageable pageable) {
        Page<Specialization> page = specializationRepository.findAll(pageable);
        return page.getContent()
                .stream().map(specializationMapper::toSpecializationResponse).toList();
    }

    public SpecializationResponse getSpecialization(Long id) {
        return specializationMapper.toSpecializationResponse(specializationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public SpecializationResponse updateSpecialization(Long id, SpecializationRequest request) {
        Specialization specialization = specializationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            specializationRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getSpecializationId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        specializationMapper.updateSpecialization(specialization, request);

        return specializationMapper.toSpecializationResponse(specializationRepository.save(specialization));
    }

    public void deleteSpecialization(Long id) {
        if (!specializationRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        // Check references (RESTRICT strategy)
        long refCount = employeeEducationRepository.countBySpecialization_SpecializationId(id);
        if (refCount > 0) {
            throw new CannotDeleteException(
                    "Specialization", id, "EmployeeEducation", refCount
            );
        }

        specializationRepository.deleteById(id);
    }
}

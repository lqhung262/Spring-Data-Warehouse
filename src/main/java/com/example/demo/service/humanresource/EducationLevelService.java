package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.EducationLevel.EducationLevelRequest;
import com.example.demo.dto.humanresource.EducationLevel.EducationLevelResponse;
import com.example.demo.entity.humanresource.EducationLevel;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.EducationLevelMapper;
import com.example.demo.repository.humanresource.EducationLevelRepository;
import com.example.demo.repository.humanresource.EmployeeEducationRepository;
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
public class EducationLevelService {
    final EducationLevelRepository educationLevelRepository;
    final EducationLevelMapper educationLevelMapper;
    final EmployeeEducationRepository employeeEducationRepository;

    @Value("${entities.humanresource.educationlevel}")
    private String entityName;

    public EducationLevelResponse createEducationLevel(EducationLevelRequest request) {
        // Check source_id uniqueness for create
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            educationLevelRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        EducationLevel educationLevel = educationLevelMapper.toEducationLevel(request);

        return educationLevelMapper.toEducationLevelResponse(educationLevelRepository.save(educationLevel));
    }


//    /**
//     * Xử lý Bulk Upsert
//     */
//    @Transactional
//    public List<EducationLevelResponse> bulkUpsertEducationLevels(List<EducationLevelRequest> requests) {
//
//        // Lấy tất cả educationLevelCodes từ request
//        List<String> educationLevelCodes = requests.stream()
//                .map(EducationLevelRequest::getEducationLevelCode)
//                .toList();
//
//        // Tìm tất cả các educationLevel đã tồn tại TRONG 1 CÂU QUERY
//        Map<String, EducationLevel> existingEducationLevelsMap = educationLevelRepository.findByEducationLevelCodeIn(educationLevelCodes).stream()
//                .collect(Collectors.toMap(EducationLevel::getEducationLevelCode, educationLevel -> educationLevel));
//
//        List<EducationLevel> educationLevelsToSave = new java.util.ArrayList<>();
//
//        // Lặp qua danh sách request để quyết định UPDATE hay INSERT
//        for (EducationLevelRequest request : requests) {
//            EducationLevel educationLevel = existingEducationLevelsMap.get(request.getEducationLevelCode());
//
//            if (educationLevel != null) {
//                // --- Logic UPDATE ---
//                // EducationLevel đã tồn tại -> Cập nhật
//                educationLevelMapper.updateEducationLevel(educationLevel, request);
//                educationLevelsToSave.add(educationLevel);
//            } else {
//                // --- Logic INSERT ---
//                // EducationLevel chưa tồn tại -> Tạo mới
//                EducationLevel newEducationLevel = educationLevelMapper.toEducationLevel(request);
//                educationLevelsToSave.add(newEducationLevel);
//            }
//        }
//
//        // Lưu tất cả (cả insert và update) TRONG 1 LỆNH
//        List<EducationLevel> savedEducationLevels = educationLevelRepository.saveAll(educationLevelsToSave);
//
//        // Map sang Response DTO và trả về
//        return savedEducationLevels.stream()
//                .map(educationLevelMapper::toEducationLevelResponse)
//                .toList();
//    }
//
//    /**
//     * Xử lý Bulk Delete
//     */
//    @Transactional
//    public void bulkDeleteEducationLevels(List<Long> ids) {
//        // Kiểm tra xem có bao nhiêu ID tồn tại
//        long existingCount = educationLevelRepository.countByEducationLevelIdIn(ids);
//        if (existingCount != ids.size()) {
//            // Không phải tất cả ID đều tồn tại
//            throw new NotFoundException("Some" + entityName + "s not found. Cannot complete bulk delete.");
//        }
//
//        // Xóa tất cả bằng ID trong 1 câu query (hiệu quả)
//        educationLevelRepository.deleteAllById(ids);
//    }


    public List<EducationLevelResponse> getEducationLevels(Pageable pageable) {
        Page<EducationLevel> page = educationLevelRepository.findAll(pageable);
        return page.getContent()
                .stream().map(educationLevelMapper::toEducationLevelResponse).toList();
    }

    public EducationLevelResponse getEducationLevel(Long id) {
        return educationLevelMapper.toEducationLevelResponse(educationLevelRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public EducationLevelResponse updateEducationLevel(Long id, EducationLevelRequest request) {
        EducationLevel educationLevel = educationLevelRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            educationLevelRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getEducationLevelId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        educationLevelMapper.updateEducationLevel(educationLevel, request);

        return educationLevelMapper.toEducationLevelResponse(educationLevelRepository.save(educationLevel));
    }

    public void deleteEducationLevel(Long id) {
        checkForeignKeyConstraints(id);

        educationLevelRepository.deleteById(id);
    }

    private void checkForeignKeyConstraints(Long id) {
        if (!educationLevelRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        // Check references (RESTRICT strategy)
        long refCount = employeeEducationRepository.countByEducationLevel_EducationLevelId(id);
        if (refCount > 0) {
            throw new CannotDeleteException(
                    "EducationLevel", id, "EmployeeEducation", refCount
            );
        }
    }
}

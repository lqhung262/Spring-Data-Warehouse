package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.School.SchoolRequest;
import com.example.demo.dto.humanresource.School.SchoolResponse;
import com.example.demo.entity.humanresource.School;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.SchoolMapper;
import com.example.demo.repository.humanresource.SchoolRepository;
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
public class SchoolService {
    final SchoolRepository schoolRepository;
    final SchoolMapper schoolMapper;

    @Value("${entities.humanresource.school}")
    private String entityName;

    public SchoolResponse createSchool(SchoolRequest request) {
        // Check source_id uniqueness for create
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            schoolRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        School school = schoolMapper.toSchool(request);

        return schoolMapper.toSchoolResponse(schoolRepository.save(school));
    }


    /**
     * Xử lý Bulk Upsert
     */
//    @Transactional
//    public List<SchoolResponse> bulkUpsertSchools(List<SchoolRequest> requests) {
//
//        // Lấy tất cả schoolCodes từ request
//        List<String> schoolCodes = requests.stream()
//                .map(SchoolRequest::getSchoolCode)
//                .toList();
//
//        // Tìm tất cả các school đã tồn tại TRONG 1 CÂU QUERY
//        Map<String, School> existingSchoolsMap = schoolRepository.findBySchoolCodeIn(schoolCodes).stream()
//                .collect(Collectors.toMap(School::getSchoolCode, school -> school));
//
//        List<School> schoolsToSave = new java.util.ArrayList<>();
//
//        // Lặp qua danh sách request để quyết định UPDATE hay INSERT
//        for (SchoolRequest request : requests) {
//            School school = existingSchoolsMap.get(request.getSchoolCode());
//
//            if (school != null) {
//                // --- Logic UPDATE ---
//                // School đã tồn tại -> Cập nhật
//                schoolMapper.updateSchool(school, request);
//                schoolsToSave.add(school);
//            } else {
//                // --- Logic INSERT ---
//                // School chưa tồn tại -> Tạo mới
//                School newSchool = schoolMapper.toSchool(request);
//                schoolsToSave.add(newSchool);
//            }
//        }
//
//        // Lưu tất cả (cả insert và update) TRONG 1 LỆNH
//        List<School> savedSchools = schoolRepository.saveAll(schoolsToSave);
//
//        // Map sang Response DTO và trả về
//        return savedSchools.stream()
//                .map(schoolMapper::toSchoolResponse)
//                .toList();
//    }
//
//    /**
//     * Xử lý Bulk Delete
//     */
//    @Transactional
//    public void bulkDeleteSchools(List<Long> ids) {
//        // Kiểm tra xem có bao nhiêu ID tồn tại
//        long existingCount = schoolRepository.countBySchoolIdIn(ids);
//        if (existingCount != ids.size()) {
//            // Không phải tất cả ID đều tồn tại
//            throw new NotFoundException("Some" + entityName + "s not found. Cannot complete bulk delete.");
//        }
//
//        // Xóa tất cả bằng ID trong 1 câu query (hiệu quả)
//        schoolRepository.deleteAllById(ids);
//    }
    public List<SchoolResponse> getSchools(Pageable pageable) {
        Page<School> page = schoolRepository.findAll(pageable);
        return page.getContent()
                .stream().map(schoolMapper::toSchoolResponse).toList();
    }

    public SchoolResponse getSchool(Long id) {
        return schoolMapper.toSchoolResponse(schoolRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public SchoolResponse updateSchool(Long id, SchoolRequest request) {
        School school = schoolRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            schoolRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getSchoolId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        schoolMapper.updateSchool(school, request);

        return schoolMapper.toSchoolResponse(schoolRepository.save(school));
    }

    public void deleteSchool(Long id) {
        School school = schoolRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        schoolRepository.deleteById(id);
    }
}

package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.Major.MajorRequest;
import com.example.demo.dto.humanresource.Major.MajorResponse;
import com.example.demo.entity.humanresource.Major;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.MajorMapper;
import com.example.demo.repository.humanresource.MajorRepository;
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
public class MajorService {
    final MajorRepository majorRepository;
    final MajorMapper majorMapper;
    final EmployeeEducationRepository employeeEducationRepository;

    @Value("${entities.humanresource.major}")
    private String entityName;

    public MajorResponse createMajor(MajorRequest request) {
        // Check source_id uniqueness for create
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            majorRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        Major major = majorMapper.toMajor(request);

        return majorMapper.toMajorResponse(majorRepository.save(major));
    }

    /**
     * Xử lý Bulk Upsert
     */
//    @Transactional
//    public List<MajorResponse> bulkUpsertMajors(List<MajorRequest> requests) {
//
//        // Lấy tất cả majorCodes từ request
//        List<String> majorCodes = requests.stream()
//                .map(MajorRequest::getMajorCode)
//                .toList();
//
//        // Tìm tất cả các major đã tồn tại TRONG 1 CÂU QUERY
//        Map<String, Major> existingMajorsMap = majorRepository.findByMajorCodeIn(majorCodes).stream()
//                .collect(Collectors.toMap(Major::getMajorCode, major -> major));
//
//        List<Major> majorsToSave = new java.util.ArrayList<>();
//
//        // Lặp qua danh sách request để quyết định UPDATE hay INSERT
//        for (MajorRequest request : requests) {
//            Major major = existingMajorsMap.get(request.getMajorCode());
//
//            if (major != null) {
//                // --- Logic UPDATE ---
//                // Major đã tồn tại -> Cập nhật
//                majorMapper.updateMajor(major, request);
//                majorsToSave.add(major);
//            } else {
//                // --- Logic INSERT ---
//                // Major chưa tồn tại -> Tạo mới
//                Major newMajor = majorMapper.toMajor(request);
//                majorsToSave.add(newMajor);
//            }
//        }
//
//        // Lưu tất cả (cả insert và update) TRONG 1 LỆNH
//        List<Major> savedMajors = majorRepository.saveAll(majorsToSave);
//
//        // Map sang Response DTO và trả về
//        return savedMajors.stream()
//                .map(majorMapper::toMajorResponse)
//                .toList();
//    }
//
//    /**
//     * Xử lý Bulk Delete
//     */
//    @Transactional
//    public void bulkDeleteMajors(List<Long> ids) {
//        // Kiểm tra xem có bao nhiêu ID tồn tại
//        long existingCount = majorRepository.countByMajorIdIn(ids);
//        if (existingCount != ids.size()) {
//            // Không phải tất cả ID đều tồn tại
//            throw new NotFoundException("Some" + entityName + "s not found. Cannot complete bulk delete.");
//        }
//
//        // Xóa tất cả bằng ID trong 1 câu query (hiệu quả)
//        majorRepository.deleteAllById(ids);
//    }
    public List<MajorResponse> getMajors(Pageable pageable) {
        Page<Major> page = majorRepository.findAll(pageable);
        return page.getContent()
                .stream().map(majorMapper::toMajorResponse).toList();
    }

    public MajorResponse getMajor(Long id) {
        return majorMapper.toMajorResponse(majorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public MajorResponse updateMajor(Long id, MajorRequest request) {
        Major major = majorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            majorRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getMajorId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        majorMapper.updateMajor(major, request);

        return majorMapper.toMajorResponse(majorRepository.save(major));
    }

    public void deleteMajor(Long id) {
        checkReferenceBeforeDelete(id);

        majorRepository.deleteById(id);
    }

    private void checkReferenceBeforeDelete(Long id) {
        if (!majorRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        // Check references (RESTRICT strategy)
        long refCount = employeeEducationRepository.countByMajor_MajorId(id);
        if (refCount > 0) {
            throw new CannotDeleteException(
                    "Major", id, "EmployeeEducation", refCount
            );
        }
    }
}

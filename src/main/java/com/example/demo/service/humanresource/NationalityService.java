package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.Nationality.NationalityRequest;
import com.example.demo.dto.humanresource.Nationality.NationalityResponse;
import com.example.demo.entity.humanresource.Nationality;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.NationalityMapper;
import com.example.demo.repository.humanresource.NationalityRepository;
import com.example.demo.repository.humanresource.EmployeeRepository;
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
public class NationalityService {
    final NationalityRepository nationalityRepository;
    final NationalityMapper nationalityMapper;
    final EmployeeRepository employeeRepository;

    @Value("${entities.humanresource.nationality}")
    private String entityName;

    public NationalityResponse createNationality(NationalityRequest request) {
        // Check source_id uniqueness for create
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            nationalityRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        Nationality nationality = nationalityMapper.toNationality(request);

        return nationalityMapper.toNationalityResponse(nationalityRepository.save(nationality));
    }

    /**
     * Xử lý Bulk Upsert
     */
//    @Transactional
//    public List<NationalityResponse> bulkUpsertNationalities(List<NationalityRequest> requests) {
//
//        // Lấy tất cả nationalityCodes từ request
//        List<String> nationalityCodes = requests.stream()
//                .map(NationalityRequest::getNationalityCode)
//                .toList();
//
//        // Tìm tất cả các nationality đã tồn tại TRONG 1 CÂU QUERY
//        Map<String, Nationality> existingNationalitiesMap = nationalityRepository.findByNationalityCodeIn(nationalityCodes).stream()
//                .collect(Collectors.toMap(Nationality::getNationalityCode, nationality -> nationality));
//
//        List<Nationality> nationalitiesToSave = new java.util.ArrayList<>();
//
//        // Lặp qua danh sách request để quyết định UPDATE hay INSERT
//        for (NationalityRequest request : requests) {
//            Nationality nationality = existingNationalitiesMap.get(request.getNationalityCode());
//
//            if (nationality != null) {
//                // --- Logic UPDATE ---
//                // Nationality đã tồn tại -> Cập nhật
//                nationalityMapper.updateNationality(nationality, request);
//                nationalitiesToSave.add(nationality);
//            } else {
//                // --- Logic INSERT ---
//                // Nationality chưa tồn tại -> Tạo mới
//                Nationality newNationality = nationalityMapper.toNationality(request);
//                nationalitiesToSave.add(newNationality);
//            }
//        }
//
//        // Lưu tất cả (cả insert và update) TRONG 1 LỆNH
//        List<Nationality> savedNationalities = nationalityRepository.saveAll(nationalitiesToSave);
//
//        // Map sang Response DTO và trả về
//        return savedNationalities.stream()
//                .map(nationalityMapper::toNationalityResponse)
//                .toList();
//    }
//
//    /**
//     * Xử lý Bulk Delete
//     */
//    @Transactional
//    public void bulkDeleteNationalities(List<Long> ids) {
//        // Kiểm tra xem có bao nhiêu ID tồn tại
//        long existingCount = nationalityRepository.countByNationalityIdIn(ids);
//        if (existingCount != ids.size()) {
//            // Không phải tất cả ID đều tồn tại
//            throw new NotFoundException("Some" + entityName + "s not found. Cannot complete bulk delete.");
//        }
//
//        // Xóa tất cả bằng ID trong 1 câu query (hiệu quả)
//        nationalityRepository.deleteAllById(ids);
//    }
    public List<NationalityResponse> getNationalities(Pageable pageable) {
        Page<Nationality> page = nationalityRepository.findAll(pageable);
        return page.getContent()
                .stream().map(nationalityMapper::toNationalityResponse).toList();
    }

    public NationalityResponse getNationality(Long id) {
        return nationalityMapper.toNationalityResponse(nationalityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public NationalityResponse updateNationality(Long id, NationalityRequest request) {
        Nationality nationality = nationalityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            nationalityRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getNationalityId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        nationalityMapper.updateNationality(nationality, request);

        return nationalityMapper.toNationalityResponse(nationalityRepository.save(nationality));
    }

    public void deleteNationality(Long id) {
        checkForeignKeyConstraints(id);

        nationalityRepository.deleteById(id);
    }

    private void checkForeignKeyConstraints(Long id) {
        if (!nationalityRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        // Check references (RESTRICT strategy)
        long refCount = employeeRepository.countByNationality_NationalityId(id);
        if (refCount > 0) {
            throw new CannotDeleteException(
                    "Nationality", id, "Employee", refCount
            );
        }
    }
}

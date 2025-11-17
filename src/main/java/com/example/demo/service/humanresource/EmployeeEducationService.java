package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.EmployeeEducation.EmployeeEducationRequest;
import com.example.demo.dto.humanresource.EmployeeEducation.EmployeeEducationResponse;
import com.example.demo.entity.humanresource.EmployeeEducation;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.EmployeeEducationMapper;
import com.example.demo.repository.humanresource.EmployeeEducationRepository;
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
public class EmployeeEducationService {
    final EmployeeEducationRepository employeeEducationRepository;
    final EmployeeEducationMapper employeeEducationMapper;

    @Value("${entities.humanresource.employeeeducation}")
    private String entityName;

    public EmployeeEducationResponse createEmployeeEducation(EmployeeEducationRequest request) {
        EmployeeEducation employeeEducation = employeeEducationMapper.toEmployeeEducation(request);

        return employeeEducationMapper.toEmployeeEducationResponse(employeeEducationRepository.save(employeeEducation));
    }


    /**
     * Xử lý Bulk Upsert
     */
//    @Transactional
//    public List<EmployeeEducationResponse> bulkUpsertEmployeeEducations(List<EmployeeEducationRequest> requests) {
//
//        // Lấy tất cả employeeEducationCodes từ request
//        List<String> employeeEducationCodes = requests.stream()
//                .map(EmployeeEducationRequest::getEmployeeEducationCode)
//                .toList();
//
//        // Tìm tất cả các employeeEducation đã tồn tại TRONG 1 CÂU QUERY
//        Map<String, EmployeeEducation> existingEmployeeEducationsMap = employeeEducationRepository.findByEmployeeEducationCodeIn(employeeEducationCodes).stream()
//                .collect(Collectors.toMap(EmployeeEducation::getEmployeeEducationCode, employeeEducation -> employeeEducation));
//
//        List<EmployeeEducation> employeeEducationsToSave = new java.util.ArrayList<>();
//
//        // Lặp qua danh sách request để quyết định UPDATE hay INSERT
//        for (EmployeeEducationRequest request : requests) {
//            EmployeeEducation employeeEducation = existingEmployeeEducationsMap.get(request.getEmployeeEducationCode());
//
//            if (employeeEducation != null) {
//                // --- Logic UPDATE ---
//                // EmployeeEducation đã tồn tại -> Cập nhật
//                employeeEducationMapper.updateEmployeeEducation(employeeEducation, request);
//                employeeEducationsToSave.add(employeeEducation);
//            } else {
//                // --- Logic INSERT ---
//                // EmployeeEducation chưa tồn tại -> Tạo mới
//                EmployeeEducation newEmployeeEducation = employeeEducationMapper.toEmployeeEducation(request);
//                employeeEducationsToSave.add(newEmployeeEducation);
//            }
//        }
//
//        // Lưu tất cả (cả insert và update) TRONG 1 LỆNH
//        List<EmployeeEducation> savedEmployeeEducations = employeeEducationRepository.saveAll(employeeEducationsToSave);
//
//        // Map sang Response DTO và trả về
//        return savedEmployeeEducations.stream()
//                .map(employeeEducationMapper::toEmployeeEducationResponse)
//                .toList();
//    }
//
//    /**
//     * Xử lý Bulk Delete
//     */
//    @Transactional
//    public void bulkDeleteEmployeeEducations(List<Long> ids) {
//        // Kiểm tra xem có bao nhiêu ID tồn tại
//        long existingCount = employeeEducationRepository.countByEmployeeEducationIdIn(ids);
//        if (existingCount != ids.size()) {
//            // Không phải tất cả ID đều tồn tại
//            throw new NotFoundException("Some" + entityName + "s not found. Cannot complete bulk delete.");
//        }
//
//        // Xóa tất cả bằng ID trong 1 câu query (hiệu quả)
//        employeeEducationRepository.deleteAllById(ids);
//    }
    public List<EmployeeEducationResponse> getEmployeeEducations(Pageable pageable) {
        Page<EmployeeEducation> page = employeeEducationRepository.findAll(pageable);
        return page.getContent()
                .stream().map(employeeEducationMapper::toEmployeeEducationResponse).toList();
    }

    public EmployeeEducationResponse getEmployeeEducation(Long id) {
        return employeeEducationMapper.toEmployeeEducationResponse(employeeEducationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public EmployeeEducationResponse updateEmployeeEducation(Long id, EmployeeEducationRequest request) {
        EmployeeEducation employeeEducation = employeeEducationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        employeeEducationMapper.updateEmployeeEducation(employeeEducation, request);

        return employeeEducationMapper.toEmployeeEducationResponse(employeeEducationRepository.save(employeeEducation));
    }

    public void deleteEmployeeEducation(Long id) {
        EmployeeEducation employeeEducation = employeeEducationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        employeeEducationRepository.deleteById(id);
    }
}

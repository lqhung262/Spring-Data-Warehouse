package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.EmployeeType.EmployeeTypeRequest;
import com.example.demo.dto.humanresource.EmployeeType.EmployeeTypeResponse;
import com.example.demo.entity.humanresource.EmployeeType;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.EmployeeTypeMapper;
import com.example.demo.repository.humanresource.EmployeeTypeRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.demo.repository.humanresource.EmployeeDecisionRepository;
import com.example.demo.exception.CannotDeleteException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeTypeService {
    final EmployeeTypeRepository employeeTypeRepository;
    final EmployeeTypeMapper employeeTypeMapper;
    final EmployeeDecisionRepository employeeDecisionRepository;

    @Value("${entities.humanresource.employeetype}")
    private String entityName;


    public EmployeeTypeResponse createEmployeeType(EmployeeTypeRequest request) {
        // Check source_id uniqueness for create
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            employeeTypeRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        EmployeeType employeeType = employeeTypeMapper.toEmployeeType(request);

        return employeeTypeMapper.toEmployeeTypeResponse(employeeTypeRepository.save(employeeType));
    }


    /**
     * Xử lý Bulk Upsert
     */
//    @Transactional
//    public List<EmployeeTypeResponse> bulkUpsertEmployeeTypes(List<EmployeeTypeRequest> requests) {
//
//        // Lấy tất cả employeeTypeCodes từ request
//        List<String> employeeTypeCodes = requests.stream()
//                .map(EmployeeTypeRequest::getEmployeeTypeCode)
//                .toList();
//
//        // Tìm tất cả các employeeType đã tồn tại TRONG 1 CÂU QUERY
//        Map<String, EmployeeType> existingEmployeeTypesMap = employeeTypeRepository.findByEmployeeTypeCodeIn(employeeTypeCodes).stream()
//                .collect(Collectors.toMap(EmployeeType::getEmployeeTypeCode, employeeType -> employeeType));
//
//        List<EmployeeType> employeeTypesToSave = new java.util.ArrayList<>();
//
//        // Lặp qua danh sách request để quyết định UPDATE hay INSERT
//        for (EmployeeTypeRequest request : requests) {
//            EmployeeType employeeType = existingEmployeeTypesMap.get(request.getEmployeeTypeCode());
//
//            if (employeeType != null) {
//                // --- Logic UPDATE ---
//                // EmployeeType đã tồn tại -> Cập nhật
//                employeeTypeMapper.updateEmployeeType(employeeType, request);
//                employeeTypesToSave.add(employeeType);
//            } else {
//                // --- Logic INSERT ---
//                // EmployeeType chưa tồn tại -> Tạo mới
//                EmployeeType newEmployeeType = employeeTypeMapper.toEmployeeType(request);
//                employeeTypesToSave.add(newEmployeeType);
//            }
//        }
//
//        // Lưu tất cả (cả insert và update) TRONG 1 LỆNH
//        List<EmployeeType> savedEmployeeTypes = employeeTypeRepository.saveAll(employeeTypesToSave);
//
//        // Map sang Response DTO và trả về
//        return savedEmployeeTypes.stream()
//                .map(employeeTypeMapper::toEmployeeTypeResponse)
//                .toList();
//    }
//
//    /**
//     * Xử lý Bulk Delete
//     */
//    @Transactional
//    public void bulkDeleteEmployeeTypes(List<Long> ids) {
//        // Kiểm tra xem có bao nhiêu ID tồn tại
//        long existingCount = employeeTypeRepository.countByEmployeeTypeIdIn(ids);
//        if (existingCount != ids.size()) {
//            // Không phải tất cả ID đều tồn tại
//            throw new NotFoundException("Some" + entityName + "s not found. Cannot complete bulk delete.");
//        }
//
//        // Xóa tất cả bằng ID trong 1 câu query (hiệu quả)
//        employeeTypeRepository.deleteAllById(ids);
//    }
    public List<EmployeeTypeResponse> getEmployeeTypes(Pageable pageable) {
        Page<EmployeeType> page = employeeTypeRepository.findAll(pageable);
        return page.getContent()
                .stream().map(employeeTypeMapper::toEmployeeTypeResponse).toList();
    }

    public EmployeeTypeResponse getEmployeeType(Long id) {
        return employeeTypeMapper.toEmployeeTypeResponse(employeeTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public EmployeeTypeResponse updateEmployeeType(Long id, EmployeeTypeRequest request) {
        EmployeeType employeeType = employeeTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            employeeTypeRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getEmployeeTypeId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        employeeTypeMapper.updateEmployeeType(employeeType, request);

        return employeeTypeMapper.toEmployeeTypeResponse(employeeTypeRepository.save(employeeType));
    }

    public void deleteEmployeeType(Long id) {
        checkForeignKeyConstraints(id);

        employeeTypeRepository.deleteById(id);
    }

    private void checkForeignKeyConstraints(Long id) {
        if (!employeeTypeRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        // Check references from EmployeeDecision
        long refCount = employeeDecisionRepository.countByEmployeeType_EmployeeTypeId(id);
        if (refCount > 0) {
            throw new CannotDeleteException(
                    "EmployeeType", id, "EmployeeDecision", refCount
            );
        }
    }
}

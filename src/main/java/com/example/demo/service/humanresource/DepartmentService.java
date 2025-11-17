package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.Department.DepartmentRequest;
import com.example.demo.dto.humanresource.Department.DepartmentResponse;
import com.example.demo.entity.humanresource.Department;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.DepartmentMapper;
import com.example.demo.repository.humanresource.DepartmentRepository;
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
public class DepartmentService {
    final DepartmentRepository departmentRepository;
    final DepartmentMapper departmentMapper;

    @Value("${entities.humanresource.department}")
    private String entityName;

    public DepartmentResponse createDepartment(DepartmentRequest request) {
//        departmentRepository.findByDepartmentCode(request.getDepartmentCode()).ifPresent(b -> {
//            throw new IllegalArgumentException(entityName + " with department Code " + request.getDepartmentCode() + " already exists.");
//        });
        Department department = departmentMapper.toDepartment(request);

        return departmentMapper.toDepartmentResponse(departmentRepository.save(department));
    }

//    /**
//     * Xử lý Bulk Upsert
//     */
//    @Transactional
//    public List<DepartmentResponse> bulkUpsertDepartments(List<DepartmentRequest> requests) {
//
//        // Lấy tất cả departmentCodes từ request
//        List<String> departmentCodes = requests.stream()
//                .map(DepartmentRequest::getDepartmentCode)
//                .toList();
//
//        // Tìm tất cả các department đã tồn tại TRONG 1 CÂU QUERY
//        Map<String, Department> existingDepartmentsMap = departmentRepository.findByDepartmentCodeIn(departmentCodes).stream()
//                .collect(Collectors.toMap(Department::getDepartmentCode, department -> department));
//
//        List<Department> departmentsToSave = new java.util.ArrayList<>();
//
//        // Lặp qua danh sách request để quyết định UPDATE hay INSERT
//        for (DepartmentRequest request : requests) {
//            Department department = existingDepartmentsMap.get(request.getDepartmentCode());
//
//            if (department != null) {
//                // --- Logic UPDATE ---
//                // Department đã tồn tại -> Cập nhật
//                departmentMapper.updateDepartment(department, request);
//                departmentsToSave.add(department);
//            } else {
//                // --- Logic INSERT ---
//                // Department chưa tồn tại -> Tạo mới
//                Department newDepartment = departmentMapper.toDepartment(request);
//                departmentsToSave.add(newDepartment);
//            }
//        }
//
//        // Lưu tất cả (cả insert và update) TRONG 1 LỆNH
//        List<Department> savedDepartments = departmentRepository.saveAll(departmentsToSave);
//
//        // Map sang Response DTO và trả về
//        return savedDepartments.stream()
//                .map(departmentMapper::toDepartmentResponse)
//                .toList();
//    }
//
//    /**
//     * Xử lý Bulk Delete
//     */
//    @Transactional
//    public void bulkDeleteDepartments(List<Long> ids) {
//        // Kiểm tra xem có bao nhiêu ID tồn tại
//        long existingCount = departmentRepository.countByDepartmentIdIn(ids);
//        if (existingCount != ids.size()) {
//            // Không phải tất cả ID đều tồn tại
//            throw new NotFoundException("Some" + entityName + "s not found. Cannot complete bulk delete.");
//        }
//
//        // Xóa tất cả bằng ID trong 1 câu query (hiệu quả)
//        departmentRepository.deleteAllById(ids);
//    }


    public List<DepartmentResponse> getDepartments(Pageable pageable) {
        Page<Department> page = departmentRepository.findAll(pageable);
        return page.getContent()
                .stream().map(departmentMapper::toDepartmentResponse).toList();
    }

    public DepartmentResponse getDepartment(Long id) {
        return departmentMapper.toDepartmentResponse(departmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public DepartmentResponse updateDepartment(Long id, DepartmentRequest request) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        departmentMapper.updateDepartment(department, request);

        return departmentMapper.toDepartmentResponse(departmentRepository.save(department));
    }

    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        departmentRepository.deleteById(id);
    }
}

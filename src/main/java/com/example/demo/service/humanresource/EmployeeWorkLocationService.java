package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.EmployeeWorkLocation.EmployeeWorkLocationRequest;
import com.example.demo.dto.humanresource.EmployeeWorkLocation.EmployeeWorkLocationResponse;
import com.example.demo.entity.humanresource.EmployeeWorkLocation;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.EmployeeWorkLocationMapper;
import com.example.demo.repository.humanresource.EmployeeWorkLocationRepository;
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
public class EmployeeWorkLocationService {
    final EmployeeWorkLocationRepository employeeWorkLocationRepository;
    final EmployeeWorkLocationMapper employeeWorkLocationMapper;

    @Value("${entities.humanresource.employeeworklocation}")
    private String entityName;

    public EmployeeWorkLocationResponse createEmployeeWorkLocation(EmployeeWorkLocationRequest request) {
        EmployeeWorkLocation employeeWorkLocation = employeeWorkLocationMapper.toEmployeeWorkLocation(request);

        return employeeWorkLocationMapper.toEmployeeWorkLocationResponse(employeeWorkLocationRepository.save(employeeWorkLocation));
    }

    /**
     * Xử lý Bulk Upsert
     */
//    @Transactional
//    public List<EmployeeWorkLocationResponse> bulkUpsertEmployeeWorkLocations(List<EmployeeWorkLocationRequest> requests) {
//
//        // Lấy tất cả employeeWorkLocationCodes từ request
//        List<String> employeeWorkLocationCodes = requests.stream()
//                .map(EmployeeWorkLocationRequest::getEmployeeWorkLocationCode)
//                .toList();
//
//        // Tìm tất cả các employeeWorkLocation đã tồn tại TRONG 1 CÂU QUERY
//        Map<String, EmployeeWorkLocation> existingEmployeeWorkLocationsMap = employeeWorkLocationRepository.findByEmployeeWorkLocationCodeIn(employeeWorkLocationCodes).stream()
//                .collect(Collectors.toMap(EmployeeWorkLocation::getEmployeeWorkLocationCode, employeeWorkLocation -> employeeWorkLocation));
//
//        List<EmployeeWorkLocation> employeeWorkLocationsToSave = new java.util.ArrayList<>();
//
//        // Lặp qua danh sách request để quyết định UPDATE hay INSERT
//        for (EmployeeWorkLocationRequest request : requests) {
//            EmployeeWorkLocation employeeWorkLocation = existingEmployeeWorkLocationsMap.get(request.getEmployeeWorkLocationCode());
//
//            if (employeeWorkLocation != null) {
//                // --- Logic UPDATE ---
//                // EmployeeWorkLocation đã tồn tại -> Cập nhật
//                employeeWorkLocationMapper.updateEmployeeWorkLocation(employeeWorkLocation, request);
//                employeeWorkLocationsToSave.add(employeeWorkLocation);
//            } else {
//                // --- Logic INSERT ---
//                // EmployeeWorkLocation chưa tồn tại -> Tạo mới
//                EmployeeWorkLocation newEmployeeWorkLocation = employeeWorkLocationMapper.toEmployeeWorkLocation(request);
//                employeeWorkLocationsToSave.add(newEmployeeWorkLocation);
//            }
//        }
//
//        // Lưu tất cả (cả insert và update) TRONG 1 LỆNH
//        List<EmployeeWorkLocation> savedEmployeeWorkLocations = employeeWorkLocationRepository.saveAll(employeeWorkLocationsToSave);
//
//        // Map sang Response DTO và trả về
//        return savedEmployeeWorkLocations.stream()
//                .map(employeeWorkLocationMapper::toEmployeeWorkLocationResponse)
//                .toList();
//    }
//
//    /**
//     * Xử lý Bulk Delete
//     */
//    @Transactional
//    public void bulkDeleteEmployeeWorkLocations(List<Long> ids) {
//        // Kiểm tra xem có bao nhiêu ID tồn tại
//        long existingCount = employeeWorkLocationRepository.countByEmployeeWorkLocationIdIn(ids);
//        if (existingCount != ids.size()) {
//            // Không phải tất cả ID đều tồn tại
//            throw new NotFoundException("Some" + entityName + "s not found. Cannot complete bulk delete.");
//        }
//
//        // Xóa tất cả bằng ID trong 1 câu query (hiệu quả)
//        employeeWorkLocationRepository.deleteAllById(ids);
//    }
    public List<EmployeeWorkLocationResponse> getEmployeeWorkLocations(Pageable pageable) {
        Page<EmployeeWorkLocation> page = employeeWorkLocationRepository.findAll(pageable);
        return page.getContent()
                .stream().map(employeeWorkLocationMapper::toEmployeeWorkLocationResponse).toList();
    }

    public EmployeeWorkLocationResponse getEmployeeWorkLocation(Long id) {
        return employeeWorkLocationMapper.toEmployeeWorkLocationResponse(employeeWorkLocationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public EmployeeWorkLocationResponse updateEmployeeWorkLocation(Long id, EmployeeWorkLocationRequest request) {
        EmployeeWorkLocation employeeWorkLocation = employeeWorkLocationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        employeeWorkLocationMapper.updateEmployeeWorkLocation(employeeWorkLocation, request);

        return employeeWorkLocationMapper.toEmployeeWorkLocationResponse(employeeWorkLocationRepository.save(employeeWorkLocation));
    }

    public void deleteEmployeeWorkLocation(Long id) {
        EmployeeWorkLocation employeeWorkLocation = employeeWorkLocationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        employeeWorkLocationRepository.deleteById(id);
    }

}

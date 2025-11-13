package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.EmployeeWorkShift.EmployeeWorkShiftRequest;
import com.example.demo.dto.humanresource.EmployeeWorkShift.EmployeeWorkShiftResponse;
import com.example.demo.entity.humanresource.EmployeeWorkShift;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.EmployeeWorkShiftMapper;
import com.example.demo.repository.humanresource.EmployeeWorkShiftRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeWorkShiftService {
    final EmployeeWorkShiftRepository employeeWorkShiftRepository;
    final EmployeeWorkShiftMapper employeeWorkShiftMapper;

    @Value("${entities.humanresource.employeeworkshift}")
    private String entityName;

    public EmployeeWorkShiftResponse createEmployeeWorkShift(EmployeeWorkShiftRequest request) {
        EmployeeWorkShift employeeWorkShift = employeeWorkShiftMapper.toEmployeeWorkShift(request);

        return employeeWorkShiftMapper.toEmployeeWorkShiftResponse(employeeWorkShiftRepository.save(employeeWorkShift));
    }


    /**
     * Xử lý Bulk Upsert
     */
    @Transactional
    public List<EmployeeWorkShiftResponse> bulkUpsertEmployeeWorkShifts(List<EmployeeWorkShiftRequest> requests) {

        // Lấy tất cả employeeWorkShiftCodes từ request
        List<String> employeeWorkShiftCodes = requests.stream()
                .map(EmployeeWorkShiftRequest::getEmployeeWorkShiftCode)
                .toList();

        // Tìm tất cả các employeeWorkShift đã tồn tại TRONG 1 CÂU QUERY
        Map<String, EmployeeWorkShift> existingEmployeeWorkShiftsMap = employeeWorkShiftRepository.findByEmployeeWorkShiftCodeIn(employeeWorkShiftCodes).stream()
                .collect(Collectors.toMap(EmployeeWorkShift::getAttemdanceCode, employeeWorkShift -> employeeWorkShift));

        List<EmployeeWorkShift> employeeWorkShiftsToSave = new java.util.ArrayList<>();

        // Lặp qua danh sách request để quyết định UPDATE hay INSERT
        for (EmployeeWorkShiftRequest request : requests) {
            EmployeeWorkShift employeeWorkShift = existingEmployeeWorkShiftsMap.get(request.getEmployeeWorkShiftCode());

            if (employeeWorkShift != null) {
                // --- Logic UPDATE ---
                // EmployeeWorkShift đã tồn tại -> Cập nhật
                employeeWorkShiftMapper.updateEmployeeWorkShift(employeeWorkShift, request);
                employeeWorkShiftsToSave.add(employeeWorkShift);
            } else {
                // --- Logic INSERT ---
                // EmployeeWorkShift chưa tồn tại -> Tạo mới
                EmployeeWorkShift newEmployeeWorkShift = employeeWorkShiftMapper.toEmployeeWorkShift(request);
                employeeWorkShiftsToSave.add(newEmployeeWorkShift);
            }
        }

        // Lưu tất cả (cả insert và update) TRONG 1 LỆNH
        List<EmployeeWorkShift> savedEmployeeWorkShifts = employeeWorkShiftRepository.saveAll(employeeWorkShiftsToSave);

        // Map sang Response DTO và trả về
        return savedEmployeeWorkShifts.stream()
                .map(employeeWorkShiftMapper::toEmployeeWorkShiftResponse)
                .toList();
    }

    /**
     * Xử lý Bulk Delete
     */
    @Transactional
    public void bulkDeleteEmployeeWorkShifts(List<Long> ids) {
        // Kiểm tra xem có bao nhiêu ID tồn tại
        long existingCount = employeeWorkShiftRepository.countByEmployeeWorkShiftIdIn(ids);
        if (existingCount != ids.size()) {
            // Không phải tất cả ID đều tồn tại
            throw new NotFoundException("Some" + entityName + "s not found. Cannot complete bulk delete.");
        }

        // Xóa tất cả bằng ID trong 1 câu query (hiệu quả)
        employeeWorkShiftRepository.deleteAllById(ids);
    }


    public List<EmployeeWorkShiftResponse> getEmployeeWorkShifts(Pageable pageable) {
        return employeeWorkShiftRepository.findAll(pageable).getContent().stream().map(employeeWorkShiftMapper::toEmployeeWorkShiftResponse).toList();
    }

    public EmployeeWorkShiftResponse getEmployeeWorkShift(Long id) {
        return employeeWorkShiftMapper.toEmployeeWorkShiftResponse(employeeWorkShiftRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public EmployeeWorkShiftResponse updateEmployeeWorkShift(Long id, EmployeeWorkShiftRequest request) {
        EmployeeWorkShift employeeWorkShift = employeeWorkShiftRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        employeeWorkShiftMapper.updateEmployeeWorkShift(employeeWorkShift, request);

        return employeeWorkShiftMapper.toEmployeeWorkShiftResponse(employeeWorkShiftRepository.save(employeeWorkShift));
    }

    public void deleteEmployeeWorkShift(Long employeeId) {
        EmployeeWorkShift employeeWorkShift = employeeWorkShiftRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException(entityName));
        employeeWorkShiftRepository.deleteById(employeeId);
    }
}

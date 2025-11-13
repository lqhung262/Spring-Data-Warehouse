package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.AttendanceType.AttendanceTypeRequest;
import com.example.demo.dto.humanresource.AttendanceType.AttendanceTypeResponse;
import com.example.demo.entity.humanresource.AttendanceType;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.AttendanceTypeMapper;
import com.example.demo.repository.humanresource.AttendanceTypeRepository;
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
public class AttendanceTypeService {
    final AttendanceTypeRepository attendanceTypeRepository;
    final AttendanceTypeMapper attendanceTypeMapper;

    @Value("${entities.humanresource.attendancetype}")
    private String entityName;

    public AttendanceTypeResponse createAttendanceType(AttendanceTypeRequest request) {
        attendanceTypeRepository.findByAttendanceTypeCode(request.getAttendanceTypeCode()).ifPresent(b -> {
            throw new IllegalArgumentException(entityName + " with Attendance Type Code " + request.getAttendanceTypeCode() + " already exists.");
        });

        AttendanceType attendanceType = attendanceTypeMapper.toAttendanceType(request);

        return attendanceTypeMapper.toAttendanceTypeResponse(attendanceTypeRepository.save(attendanceType));
    }

    /**
     * Xử lý Bulk Upsert
     */
    @Transactional
    public List<AttendanceTypeResponse> bulkUpsertAttendanceTypes(List<AttendanceTypeRequest> requests) {

        // Lấy tất cả attendanceTypeCodes từ request
        List<String> attendanceTypeCodes = requests.stream()
                .map(AttendanceTypeRequest::getAttendanceTypeCode)
                .toList();

        // Tìm tất cả các attendanceType đã tồn tại TRONG 1 CÂU QUERY
        Map<String, AttendanceType> existingAttendanceTypesMap = attendanceTypeRepository.findByAttendanceTypeCodeIn(attendanceTypeCodes).stream()
                .collect(Collectors.toMap(AttendanceType::getAttendanceTypeCode, attendanceType -> attendanceType));

        List<AttendanceType> attendanceTypesToSave = new java.util.ArrayList<>();

        // Lặp qua danh sách request để quyết định UPDATE hay INSERT
        for (AttendanceTypeRequest request : requests) {
            AttendanceType attendanceType = existingAttendanceTypesMap.get(request.getAttendanceTypeCode());

            if (attendanceType != null) {
                // --- Logic UPDATE ---
                // AttendanceType đã tồn tại -> Cập nhật
                attendanceTypeMapper.updateAttendanceType(attendanceType, request);
                attendanceTypesToSave.add(attendanceType);
            } else {
                // --- Logic INSERT ---
                // AttendanceType chưa tồn tại -> Tạo mới
                AttendanceType newAttendanceType = attendanceTypeMapper.toAttendanceType(request);
                attendanceTypesToSave.add(newAttendanceType);
            }
        }

        // Lưu tất cả (cả insert và update) TRONG 1 LỆNH
        List<AttendanceType> savedAttendanceTypes = attendanceTypeRepository.saveAll(attendanceTypesToSave);

        // Map sang Response DTO và trả về
        return savedAttendanceTypes.stream()
                .map(attendanceTypeMapper::toAttendanceTypeResponse)
                .toList();
    }

    /**
     * Xử lý Bulk Delete
     */
    @Transactional
    public void bulkDeleteAttendanceTypes(List<Long> ids) {
        // Kiểm tra xem có bao nhiêu ID tồn tại
        long existingCount = attendanceTypeRepository.countByAttendanceTypeIdIn(ids);
        if (existingCount != ids.size()) {
            // Không phải tất cả ID đều tồn tại
            throw new NotFoundException("Some" + entityName + "s not found. Cannot complete bulk delete.");
        }

        // Xóa tất cả bằng ID trong 1 câu query (hiệu quả)
        attendanceTypeRepository.deleteAllById(ids);
    }


    public List<AttendanceTypeResponse> getAttendanceTypes(Pageable pageable) {
        Page<AttendanceType> page = attendanceTypeRepository.findAll(pageable);
        return page.getContent()
                .stream().map(attendanceTypeMapper::toAttendanceTypeResponse).toList();
    }

    public AttendanceTypeResponse getAttendanceType(Long id) {
        return attendanceTypeMapper.toAttendanceTypeResponse(attendanceTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public AttendanceTypeResponse updateAttendanceType(Long id, AttendanceTypeRequest request) {
        AttendanceType attendanceType = attendanceTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        attendanceTypeMapper.updateAttendanceType(attendanceType, request);

        return attendanceTypeMapper.toAttendanceTypeResponse(attendanceTypeRepository.save(attendanceType));
    }

    public void deleteAttendanceType(Long id) {

        AttendanceType attendanceType = attendanceTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        attendanceTypeRepository.deleteById(id);
    }
}

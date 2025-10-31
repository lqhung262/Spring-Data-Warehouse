package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.AttendanceType.AttendanceTypeRequest;
import com.example.demo.dto.humanresource.AttendanceType.AttendanceTypeResponse;
import com.example.demo.entity.humanresource.AttendanceType;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.AttendanceTypeMapper;
import com.example.demo.repository.humanresource.AttendanceTypeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttendanceTypeService {
    final AttendanceTypeRepository attendanceTypeRepository;
    final AttendanceTypeMapper attendanceTypeMapper;

    @Value("${entities.humanresource.attendancetype}")
    private String entityName;

    public AttendanceTypeResponse createAttendanceType(AttendanceTypeRequest request) {
        AttendanceType attendanceType = attendanceTypeMapper.toAttendanceType(request);

        return attendanceTypeMapper.toAttendanceTypeResponse(attendanceTypeRepository.save(attendanceType));
    }

    public List<AttendanceTypeResponse> getAttendanceTypes(Pageable pageable) {
        Page<AttendanceType> page = attendanceTypeRepository.findAll(pageable);
        List<AttendanceTypeResponse> dtos = page.getContent()
                .stream().map(attendanceTypeMapper::toAttendanceTypeResponse).toList();
        return dtos;
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

package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.WorkShift.WorkShiftRequest;
import com.example.demo.dto.humanresource.WorkShift.WorkShiftResponse;
import com.example.demo.entity.humanresource.WorkShift;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.WorkShiftMapper;
import com.example.demo.repository.humanresource.WorkShiftRepository;
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
public class WorkShiftService {
    final WorkShiftRepository workShiftRepository;
    final WorkShiftMapper workShiftMapper;

    @Value("${entities.humanresource.workshift}")
    private String entityName;

    public WorkShiftResponse createWorkShift(WorkShiftRequest request) {
        WorkShift workShift = workShiftMapper.toWorkShift(request);

        return workShiftMapper.toWorkShiftResponse(workShiftRepository.save(workShift));
    }

    public List<WorkShiftResponse> getWorkShifts(Pageable pageable) {
        Page<WorkShift> page = workShiftRepository.findAll(pageable);
        List<WorkShiftResponse> dtos = page.getContent()
                .stream().map(workShiftMapper::toWorkShiftResponse).toList();
        return dtos;
    }

    public WorkShiftResponse getWorkShift(Long id) {
        return workShiftMapper.toWorkShiftResponse(workShiftRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public WorkShiftResponse updateWorkShift(Long id, WorkShiftRequest request) {
        WorkShift workShift = workShiftRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        workShiftMapper.updateWorkShift(workShift, request);

        return workShiftMapper.toWorkShiftResponse(workShiftRepository.save(workShift));
    }

    public void deleteWorkShift(Long id) {
        WorkShift workShift = workShiftRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        workShiftRepository.deleteById(id);
    }
}

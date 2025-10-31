package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.WorkShiftGroup.WorkShiftGroupRequest;
import com.example.demo.dto.humanresource.WorkShiftGroup.WorkShiftGroupResponse;
import com.example.demo.entity.humanresource.WorkShiftGroup;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.WorkShiftGroupMapper;
import com.example.demo.repository.humanresource.WorkShiftGroupRepository;
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
public class WorkShiftGroupService {
    final WorkShiftGroupRepository workShiftGroupRepository;
    final WorkShiftGroupMapper workShiftGroupMapper;

    @Value("${entities.humanresource.workshiftgroup}")
    private String entityName;

    public WorkShiftGroupResponse createWorkShiftGroup(WorkShiftGroupRequest request) {
        WorkShiftGroup workShiftGroup = workShiftGroupMapper.toWorkShiftGroup(request);

        return workShiftGroupMapper.toWorkShiftGroupResponse(workShiftGroupRepository.save(workShiftGroup));
    }

    public List<WorkShiftGroupResponse> getWorkShiftGroups(Pageable pageable) {
        Page<WorkShiftGroup> page = workShiftGroupRepository.findAll(pageable);
        List<WorkShiftGroupResponse> dtos = page.getContent()
                .stream().map(workShiftGroupMapper::toWorkShiftGroupResponse).toList();
        return dtos;
    }

    public WorkShiftGroupResponse getWorkShiftGroup(Long id) {
        return workShiftGroupMapper.toWorkShiftGroupResponse(workShiftGroupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public WorkShiftGroupResponse updateWorkShiftGroup(Long id, WorkShiftGroupRequest request) {
        WorkShiftGroup workShiftGroup = workShiftGroupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        workShiftGroupMapper.updateWorkShiftGroup(workShiftGroup, request);

        return workShiftGroupMapper.toWorkShiftGroupResponse(workShiftGroupRepository.save(workShiftGroup));
    }

    public void deleteWorkShiftGroup(Long id) {
        WorkShiftGroup workShiftGroup = workShiftGroupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        workShiftGroupRepository.deleteById(id);
    }
}

package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.BloodGroup.BloodGroupRequest;
import com.example.demo.dto.humanresource.BloodGroup.BloodGroupResponse;
import com.example.demo.entity.humanresource.BloodGroup;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.BloodGroupMapper;
import com.example.demo.repository.humanresource.BloodGroupRepository;
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
public class BloodGroupService {
    final BloodGroupRepository bloodGroupRepository;
    final BloodGroupMapper bloodGroupMapper;

    @Value("${entities.humanresource.bloodgroup}")
    private String entityName;

    public BloodGroupResponse createBloodGroup(BloodGroupRequest request) {
        BloodGroup bloodGroup = bloodGroupMapper.toBloodGroup(request);

        return bloodGroupMapper.toBloodGroupResponse(bloodGroupRepository.save(bloodGroup));
    }

    public List<BloodGroupResponse> getBloodGroups(Pageable pageable) {
        Page<BloodGroup> page = bloodGroupRepository.findAll(pageable);
        List<BloodGroupResponse> dtos = page.getContent()
                .stream().map(bloodGroupMapper::toBloodGroupResponse).toList();
        return dtos;
    }

    public BloodGroupResponse getBloodGroup(Long id) {
        return bloodGroupMapper.toBloodGroupResponse(bloodGroupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public BloodGroupResponse updateBloodGroup(Long id, BloodGroupRequest request) {
        BloodGroup bloodGroup = bloodGroupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        bloodGroupMapper.updateBloodGroup(bloodGroup, request);

        return bloodGroupMapper.toBloodGroupResponse(bloodGroupRepository.save(bloodGroup));
    }

    public void deleteBloodGroup(Long id) {
        BloodGroup bloodGroup = bloodGroupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        bloodGroupRepository.deleteById(id);
    }
}

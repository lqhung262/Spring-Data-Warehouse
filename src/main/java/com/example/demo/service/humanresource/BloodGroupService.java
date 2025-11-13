package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.BloodGroup.BloodGroupRequest;
import com.example.demo.dto.humanresource.BloodGroup.BloodGroupResponse;
import com.example.demo.entity.humanresource.BloodGroup;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.BloodGroupMapper;
import com.example.demo.repository.humanresource.BloodGroupRepository;
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
public class BloodGroupService {
    final BloodGroupRepository bloodGroupRepository;
    final BloodGroupMapper bloodGroupMapper;

    @Value("${entities.humanresource.bloodgroup}")
    private String entityName;

    public BloodGroupResponse createBloodGroup(BloodGroupRequest request) {
        bloodGroupRepository.findByBloodGroupCode(request.getBloodGroupCode()).ifPresent(b -> {
            throw new IllegalArgumentException(entityName + " with Blood Group Code " + request.getBloodGroupCode() + " already exists.");
        });

        BloodGroup bloodGroup = bloodGroupMapper.toBloodGroup(request);

        return bloodGroupMapper.toBloodGroupResponse(bloodGroupRepository.save(bloodGroup));
    }

    /**
     * Xử lý Bulk Upsert
     */
    @Transactional
    public List<BloodGroupResponse> bulkUpsertBloodGroups(List<BloodGroupRequest> requests) {

        // Lấy tất cả bloodGroupCodes từ request
        List<String> bloodGroupCodes = requests.stream()
                .map(BloodGroupRequest::getBloodGroupCode)
                .toList();

        // Tìm tất cả các bloodGroup đã tồn tại TRONG 1 CÂU QUERY
        Map<String, BloodGroup> existingBloodGroupsMap = bloodGroupRepository.findByBloodGroupCodeIn(bloodGroupCodes).stream()
                .collect(Collectors.toMap(BloodGroup::getBloodGroupCode, bloodGroup -> bloodGroup));

        List<BloodGroup> bloodGroupsToSave = new java.util.ArrayList<>();

        // Lặp qua danh sách request để quyết định UPDATE hay INSERT
        for (BloodGroupRequest request : requests) {
            BloodGroup bloodGroup = existingBloodGroupsMap.get(request.getBloodGroupCode());

            if (bloodGroup != null) {
                // --- Logic UPDATE ---
                // BloodGroup đã tồn tại -> Cập nhật
                bloodGroupMapper.updateBloodGroup(bloodGroup, request);
                bloodGroupsToSave.add(bloodGroup);
            } else {
                // --- Logic INSERT ---
                // BloodGroup chưa tồn tại -> Tạo mới
                BloodGroup newBloodGroup = bloodGroupMapper.toBloodGroup(request);
                bloodGroupsToSave.add(newBloodGroup);
            }
        }

        // Lưu tất cả (cả insert và update) TRONG 1 LỆNH
        List<BloodGroup> savedBloodGroups = bloodGroupRepository.saveAll(bloodGroupsToSave);

        // Map sang Response DTO và trả về
        return savedBloodGroups.stream()
                .map(bloodGroupMapper::toBloodGroupResponse)
                .toList();
    }

    /**
     * Xử lý Bulk Delete
     */
    @Transactional
    public void bulkDeleteBloodGroups(List<Long> ids) {
        // Kiểm tra xem có bao nhiêu ID tồn tại
        long existingCount = bloodGroupRepository.countByBloodGroupIdIn(ids);
        if (existingCount != ids.size()) {
            // Không phải tất cả ID đều tồn tại
            throw new NotFoundException("Some" + entityName + "s not found. Cannot complete bulk delete.");
        }

        // Xóa tất cả bằng ID trong 1 câu query (hiệu quả)
        bloodGroupRepository.deleteAllById(ids);
    }

    public List<BloodGroupResponse> getBloodGroups(Pageable pageable) {
        Page<BloodGroup> page = bloodGroupRepository.findAll(pageable);
        return page.getContent()
                .stream().map(bloodGroupMapper::toBloodGroupResponse).toList();
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

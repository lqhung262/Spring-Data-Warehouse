package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.IdentityIssuingAuthority.IdentityIssuingAuthorityRequest;
import com.example.demo.dto.humanresource.IdentityIssuingAuthority.IdentityIssuingAuthorityResponse;
import com.example.demo.entity.humanresource.IdentityIssuingAuthority;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.IdentityIssuingAuthorityMapper;
import com.example.demo.repository.humanresource.IdentityIssuingAuthorityRepository;
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
public class IdentityIssuingAuthorityService {
    final IdentityIssuingAuthorityRepository identityIssuingAuthorityRepository;
    final IdentityIssuingAuthorityMapper identityIssuingAuthorityMapper;

    @Value("${entities.humanresource.identityissuingauthoirity}")
    private String entityName;

    public IdentityIssuingAuthorityResponse createIdentityIssuingAuthority(IdentityIssuingAuthorityRequest request) {
        // Check source_id uniqueness for create
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            identityIssuingAuthorityRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        IdentityIssuingAuthority identityIssuingAuthority = identityIssuingAuthorityMapper.toIdentityIssuingAuthority(request);

        return identityIssuingAuthorityMapper.toIdentityIssuingAuthorityResponse(identityIssuingAuthorityRepository.save(identityIssuingAuthority));
    }

    /**
     * Xử lý Bulk Upsert
     */
//    @Transactional
//    public List<IdentityIssuingAuthorityResponse> bulkUpsertIdentityIssuingAuthorities(List<IdentityIssuingAuthorityRequest> requests) {
//
//        // Lấy tất cả identityIssuingAuthorityCodes từ request
//        List<String> identityIssuingAuthorityCodes = requests.stream()
//                .map(IdentityIssuingAuthorityRequest::getIdentityIssuingAuthorityCode)
//                .toList();
//
//        // Tìm tất cả các identityIssuingAuthority đã tồn tại TRONG 1 CÂU QUERY
//        Map<String, IdentityIssuingAuthority> existingIdentityIssuingAuthoritysMap = identityIssuingAuthorityRepository.findByIdentityIssuingAuthorityCodeIn(identityIssuingAuthorityCodes).stream()
//                .collect(Collectors.toMap(IdentityIssuingAuthority::getIdentityIssuingAuthorityCode, identityIssuingAuthority -> identityIssuingAuthority));
//
//        List<IdentityIssuingAuthority> identityIssuingAuthoritysToSave = new java.util.ArrayList<>();
//
//        // Lặp qua danh sách request để quyết định UPDATE hay INSERT
//        for (IdentityIssuingAuthorityRequest request : requests) {
//            IdentityIssuingAuthority identityIssuingAuthority = existingIdentityIssuingAuthoritysMap.get(request.getIdentityIssuingAuthorityCode());
//
//            if (identityIssuingAuthority != null) {
//                // --- Logic UPDATE ---
//                // IdentityIssuingAuthority đã tồn tại -> Cập nhật
//                identityIssuingAuthorityMapper.updateIdentityIssuingAuthority(identityIssuingAuthority, request);
//                identityIssuingAuthoritysToSave.add(identityIssuingAuthority);
//            } else {
//                // --- Logic INSERT ---
//                // IdentityIssuingAuthority chưa tồn tại -> Tạo mới
//                IdentityIssuingAuthority newIdentityIssuingAuthority = identityIssuingAuthorityMapper.toIdentityIssuingAuthority(request);
//                identityIssuingAuthoritysToSave.add(newIdentityIssuingAuthority);
//            }
//        }
//
//        // Lưu tất cả (cả insert và update) TRONG 1 LỆNH
//        List<IdentityIssuingAuthority> savedIdentityIssuingAuthoritys = identityIssuingAuthorityRepository.saveAll(identityIssuingAuthoritysToSave);
//
//        // Map sang Response DTO và trả về
//        return savedIdentityIssuingAuthoritys.stream()
//                .map(identityIssuingAuthorityMapper::toIdentityIssuingAuthorityResponse)
//                .toList();
//    }
//
//    /**
//     * Xử lý Bulk Delete
//     */
//    @Transactional
//    public void bulkDeleteIdentityIssuingAuthorities(List<Long> ids) {
//        // Kiểm tra xem có bao nhiêu ID tồn tại
//        long existingCount = identityIssuingAuthorityRepository.countByIdentityIssuingAuthorityIdIn(ids);
//        if (existingCount != ids.size()) {
//            // Không phải tất cả ID đều tồn tại
//            throw new NotFoundException("Some" + entityName + "s not found. Cannot complete bulk delete.");
//        }
//
//        // Xóa tất cả bằng ID trong 1 câu query (hiệu quả)
//        identityIssuingAuthorityRepository.deleteAllById(ids);
//    }
    public List<IdentityIssuingAuthorityResponse> getIdentityIssuingAuthorities(Pageable pageable) {
        Page<IdentityIssuingAuthority> page = identityIssuingAuthorityRepository.findAll(pageable);
        return page.getContent()
                .stream().map(identityIssuingAuthorityMapper::toIdentityIssuingAuthorityResponse).toList();
    }

    public IdentityIssuingAuthorityResponse getIdentityIssuingAuthority(Long id) {
        return identityIssuingAuthorityMapper.toIdentityIssuingAuthorityResponse(identityIssuingAuthorityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public IdentityIssuingAuthorityResponse updateIdentityIssuingAuthority(Long id, IdentityIssuingAuthorityRequest request) {
        IdentityIssuingAuthority identityIssuingAuthority = identityIssuingAuthorityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            identityIssuingAuthorityRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getIdentityIssuingAuthorityId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        identityIssuingAuthorityMapper.updateIdentityIssuingAuthority(identityIssuingAuthority, request);

        return identityIssuingAuthorityMapper.toIdentityIssuingAuthorityResponse(identityIssuingAuthorityRepository.save(identityIssuingAuthority));
    }

    public void deleteIdentityIssuingAuthority(Long id) {
        IdentityIssuingAuthority identityIssuingAuthority = identityIssuingAuthorityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        identityIssuingAuthorityRepository.deleteById(id);
    }
}

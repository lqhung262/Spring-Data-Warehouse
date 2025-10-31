package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.OldDistrict.OldDistrictRequest;
import com.example.demo.dto.humanresource.OldDistrict.OldDistrictResponse;
import com.example.demo.entity.humanresource.OldDistrict;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.OldDistrictMapper;
import com.example.demo.repository.humanresource.OldDistrictRepository;
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
public class OldDistrictService {
    final OldDistrictRepository oldDistrictRepository;
    final OldDistrictMapper oldDistrictMapper;

    @Value("${entities.humanresource.olddistrict}")
    private String entityName;

    public OldDistrictResponse createOldDistrict(OldDistrictRequest request) {
        OldDistrict oldDistrict = oldDistrictMapper.toOldDistrict(request);

        return oldDistrictMapper.toOldDistrictResponse(oldDistrictRepository.save(oldDistrict));
    }

    public List<OldDistrictResponse> getOldDistricts(Pageable pageable) {
        Page<OldDistrict> page = oldDistrictRepository.findAll(pageable);
        List<OldDistrictResponse> dtos = page.getContent()
                .stream().map(oldDistrictMapper::toOldDistrictResponse).toList();
        return dtos;
    }

    public OldDistrictResponse getOldDistrict(Long id) {
        return oldDistrictMapper.toOldDistrictResponse(oldDistrictRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public OldDistrictResponse updateOldDistrict(Long id, OldDistrictRequest request) {
        OldDistrict oldDistrict = oldDistrictRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        oldDistrictMapper.updateOldDistrict(oldDistrict, request);

        return oldDistrictMapper.toOldDistrictResponse(oldDistrictRepository.save(oldDistrict));
    }

    public void deleteOldDistrict(Long id) {
        OldDistrict oldDistrict = oldDistrictRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        oldDistrictRepository.deleteById(id);
    }
}

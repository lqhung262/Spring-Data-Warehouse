package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.OtType.OtTypeRequest;
import com.example.demo.dto.humanresource.OtType.OtTypeResponse;
import com.example.demo.entity.humanresource.OtType;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.OtTypeMapper;
import com.example.demo.repository.humanresource.OtTypeRepository;
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
public class OtTypeService {
    final OtTypeRepository otTypeRepository;
    final OtTypeMapper otTypeMapper;

    @Value("${entities.humanresource.ottype}")
    private String entityName;

    public OtTypeResponse createOtType(OtTypeRequest request) {
        OtType otType = otTypeMapper.toOtType(request);

        return otTypeMapper.toOtTypeResponse(otTypeRepository.save(otType));
    }

    public List<OtTypeResponse> getOtTypes(Pageable pageable) {
        Page<OtType> page = otTypeRepository.findAll(pageable);
        List<OtTypeResponse> dtos = page.getContent()
                .stream().map(otTypeMapper::toOtTypeResponse).toList();
        return dtos;
    }

    public OtTypeResponse getOtType(Long id) {
        return otTypeMapper.toOtTypeResponse(otTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public OtTypeResponse updateOtType(Long id, OtTypeRequest request) {
        OtType otType = otTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        otTypeMapper.updateOtType(otType, request);

        return otTypeMapper.toOtTypeResponse(otTypeRepository.save(otType));
    }

    public void deleteOtType(Long id) {
        OtType otType = otTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        otTypeRepository.deleteById(id);
    }
}

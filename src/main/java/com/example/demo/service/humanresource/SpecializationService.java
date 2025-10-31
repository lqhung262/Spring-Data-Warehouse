package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.Specialization.SpecializationRequest;
import com.example.demo.dto.humanresource.Specialization.SpecializationResponse;
import com.example.demo.entity.humanresource.Specialization;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.SpecializationMapper;
import com.example.demo.repository.humanresource.SpecializationRepository;
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
public class SpecializationService {
    final SpecializationRepository specializationRepository;
    final SpecializationMapper specializationMapper;

    @Value("${entities.humanresource.speicialization}")
    private String entityName;

    public SpecializationResponse createSpecialization(SpecializationRequest request) {
        Specialization specialization = specializationMapper.toSpecialization(request);

        return specializationMapper.toSpecializationResponse(specializationRepository.save(specialization));
    }

    public List<SpecializationResponse> getSpecializations(Pageable pageable) {
        Page<Specialization> page = specializationRepository.findAll(pageable);
        List<SpecializationResponse> dtos = page.getContent()
                .stream().map(specializationMapper::toSpecializationResponse).toList();
        return dtos;
    }

    public SpecializationResponse getSpecialization(Long id) {
        return specializationMapper.toSpecializationResponse(specializationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public SpecializationResponse updateSpecialization(Long id, SpecializationRequest request) {
        Specialization specialization = specializationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        specializationMapper.updateSpecialization(specialization, request);

        return specializationMapper.toSpecializationResponse(specializationRepository.save(specialization));
    }

    public void deleteSpecialization(Long id) {
        Specialization specialization = specializationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        specializationRepository.deleteById(id);
    }
}

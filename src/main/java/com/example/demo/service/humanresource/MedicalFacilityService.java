package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.MedicalFacility.MedicalFacilityRequest;
import com.example.demo.dto.humanresource.MedicalFacility.MedicalFacilityResponse;
import com.example.demo.entity.humanresource.MedicalFacility;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.MedicalFacilityMapper;
import com.example.demo.repository.humanresource.MedicalFacilityRepository;
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
public class MedicalFacilityService {
    final MedicalFacilityRepository medicalFacilityRepository;
    final MedicalFacilityMapper medicalFacilityMapper;

    @Value("${entities.humanresource.medicalfacility}")
    private String entityName;

    public MedicalFacilityResponse createMedicalFacility(MedicalFacilityRequest request) {
        MedicalFacility medicalFacility = medicalFacilityMapper.toMedicalFacility(request);

        return medicalFacilityMapper.toMedicalFacilityResponse(medicalFacilityRepository.save(medicalFacility));
    }

    public List<MedicalFacilityResponse> getMedicalFacilities(Pageable pageable) {
        Page<MedicalFacility> page = medicalFacilityRepository.findAll(pageable);
        List<MedicalFacilityResponse> dtos = page.getContent()
                .stream().map(medicalFacilityMapper::toMedicalFacilityResponse).toList();
        return dtos;
    }

    public MedicalFacilityResponse getMedicalFacility(Long id) {
        return medicalFacilityMapper.toMedicalFacilityResponse(medicalFacilityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public MedicalFacilityResponse updateMedicalFacility(Long id, MedicalFacilityRequest request) {
        MedicalFacility medicalFacility = medicalFacilityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        medicalFacilityMapper.updateMedicalFacility(medicalFacility, request);

        return medicalFacilityMapper.toMedicalFacilityResponse(medicalFacilityRepository.save(medicalFacility));
    }

    public void deleteMedicalFacility(Long id) {
        MedicalFacility medicalFacility = medicalFacilityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        medicalFacilityRepository.deleteById(id);
    }
}

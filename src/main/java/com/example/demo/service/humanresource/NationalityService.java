package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.Nationality.NationalityRequest;
import com.example.demo.dto.humanresource.Nationality.NationalityResponse;
import com.example.demo.entity.humanresource.Nationality;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.NationalityMapper;
import com.example.demo.repository.humanresource.NationalityRepository;
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
public class NationalityService {
    final NationalityRepository nationalityRepository;
    final NationalityMapper nationalityMapper;

    @Value("${entities.humanresource.nationality}")
    private String entityName;

    public NationalityResponse createNationality(NationalityRequest request) {
        Nationality nationality = nationalityMapper.toNationality(request);

        return nationalityMapper.toNationalityResponse(nationalityRepository.save(nationality));
    }

    public List<NationalityResponse> getNationalities(Pageable pageable) {
        Page<Nationality> page = nationalityRepository.findAll(pageable);
        List<NationalityResponse> dtos = page.getContent()
                .stream().map(nationalityMapper::toNationalityResponse).toList();
        return dtos;
    }

    public NationalityResponse getNationality(Long id) {
        return nationalityMapper.toNationalityResponse(nationalityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public NationalityResponse updateNationality(Long id, NationalityRequest request) {
        Nationality nationality = nationalityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        nationalityMapper.updateNationality(nationality, request);

        return nationalityMapper.toNationalityResponse(nationalityRepository.save(nationality));
    }

    public void deleteNationality(Long id) {
        Nationality nationality = nationalityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        nationalityRepository.deleteById(id);
    }
}

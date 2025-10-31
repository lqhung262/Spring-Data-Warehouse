package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.OldProvinceCity.OldProvinceCityRequest;
import com.example.demo.dto.humanresource.OldProvinceCity.OldProvinceCityResponse;
import com.example.demo.entity.humanresource.OldProvinceCity;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.OldProvinceCityMapper;
import com.example.demo.repository.humanresource.OldProvinceCityRepository;
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
public class OldProvinceCityService {
    final OldProvinceCityRepository oldProvinceCityRepository;
    final OldProvinceCityMapper oldProvinceCityMapper;

    @Value("${entities.humanresource.oldprovincecity}")
    private String entityName;


    public OldProvinceCityResponse createOldProvinceCity(OldProvinceCityRequest request) {
        OldProvinceCity oldProvinceCity = oldProvinceCityMapper.toOldProvinceCity(request);

        return oldProvinceCityMapper.toOldProvinceCityResponse(oldProvinceCityRepository.save(oldProvinceCity));
    }

    public List<OldProvinceCityResponse> getOldProvinceCities(Pageable pageable) {
        Page<OldProvinceCity> page = oldProvinceCityRepository.findAll(pageable);
        List<OldProvinceCityResponse> dtos = page.getContent()
                .stream().map(oldProvinceCityMapper::toOldProvinceCityResponse).toList();
        return dtos;
    }

    public OldProvinceCityResponse getOldProvinceCity(Long id) {
        return oldProvinceCityMapper.toOldProvinceCityResponse(oldProvinceCityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public OldProvinceCityResponse updateOldProvinceCity(Long id, OldProvinceCityRequest request) {
        OldProvinceCity oldProvinceCity = oldProvinceCityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        oldProvinceCityMapper.updateOldProvinceCity(oldProvinceCity, request);

        return oldProvinceCityMapper.toOldProvinceCityResponse(oldProvinceCityRepository.save(oldProvinceCity));
    }

    public void deleteOldProvinceCity(Long id) {
        OldProvinceCity oldProvinceCity = oldProvinceCityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        oldProvinceCityRepository.deleteById(id);
    }
}

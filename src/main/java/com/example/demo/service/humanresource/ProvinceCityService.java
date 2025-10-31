package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.ProvinceCity.ProvinceCityRequest;
import com.example.demo.dto.humanresource.ProvinceCity.ProvinceCityResponse;
import com.example.demo.entity.humanresource.ProvinceCity;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.ProvinceCityMapper;
import com.example.demo.repository.humanresource.ProvinceCityRepository;
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
public class ProvinceCityService {
    final ProvinceCityRepository ProvinceCityRepository;
    final ProvinceCityMapper ProvinceCityMapper;

    @Value("${entities.humanresource.provincecity}")
    private String entityName;

    public ProvinceCityResponse createProvinceCity(ProvinceCityRequest request) {
        ProvinceCity ProvinceCity = ProvinceCityMapper.toProvinceCity(request);

        return ProvinceCityMapper.toProvinceCityResponse(ProvinceCityRepository.save(ProvinceCity));
    }

    public List<ProvinceCityResponse> getProvinceCities(Pageable pageable) {
        Page<ProvinceCity> page = ProvinceCityRepository.findAll(pageable);
        List<ProvinceCityResponse> dtos = page.getContent()
                .stream().map(ProvinceCityMapper::toProvinceCityResponse).toList();
        return dtos;
    }

    public ProvinceCityResponse getProvinceCity(Long id) {
        return ProvinceCityMapper.toProvinceCityResponse(ProvinceCityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public ProvinceCityResponse updateProvinceCity(Long id, ProvinceCityRequest request) {
        ProvinceCity ProvinceCity = ProvinceCityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        ProvinceCityMapper.updateProvinceCity(ProvinceCity, request);

        return ProvinceCityMapper.toProvinceCityResponse(ProvinceCityRepository.save(ProvinceCity));
    }

    public void deleteProvinceCity(Long id) {
        ProvinceCity ProvinceCity = ProvinceCityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        ProvinceCityRepository.deleteById(id);
    }
}

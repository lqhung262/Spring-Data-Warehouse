package com.example.demo.service.general;

import com.example.demo.dto.general.Country.CountryRequest;
import com.example.demo.dto.general.Country.CountryResponse;
import com.example.demo.entity.general.Country;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.general.CountryMapper;
import com.example.demo.repository.general.CountryRepository;
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
public class CountryService {
    final CountryRepository countryRepository;
    final CountryMapper countryMapper;

    @Value("${entities.general.country}")
    private String entityName;


    public CountryResponse createCountry(CountryRequest request) {
        Country country = countryMapper.toCountry(request);

        return countryMapper.toCountryResponse(countryRepository.save(country));
    }

    public List<CountryResponse> getCountries(Pageable pageable) {
        Page<Country> page = countryRepository.findAll(pageable);
        List<CountryResponse> dtos = page.getContent()
                .stream().map(countryMapper::toCountryResponse).toList();
        return dtos;
    }

    public CountryResponse getCountry(Long id) {
        return countryMapper.toCountryResponse(countryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public CountryResponse updateCountry(Long id, CountryRequest request) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        countryMapper.updateCountry(country, request);

        return countryMapper.toCountryResponse(countryRepository.save(country));
    }

    public void deleteCountry(Long id) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        countryRepository.deleteById(id);
    }
}

package com.example.demo.service.general;

import com.example.demo.dto.general.Country.CountryRequest;
import com.example.demo.dto.general.Country.CountryResponse;
import com.example.demo.entity.general.Country;
import com.example.demo.exception.AlreadyExistsException;
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
        // Check source_id uniqueness for create
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            countryRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        Country country = countryMapper.toCountry(request);

        return countryMapper.toCountryResponse(countryRepository.save(country));
    }

    public List<CountryResponse> getCountries(Pageable pageable) {
        Page<Country> page = countryRepository.findAll(pageable);
        return page.getContent()
                .stream().map(countryMapper::toCountryResponse).toList();
    }

    public CountryResponse getCountry(Long id) {
        return countryMapper.toCountryResponse(countryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public CountryResponse updateCountry(Long id, CountryRequest request) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            countryRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getCountryId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        countryMapper.updateCountry(country, request);

        return countryMapper.toCountryResponse(countryRepository.save(country));
    }

    public void deleteCountry(Long id) {
        if (!countryRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        countryRepository.deleteById(id);
    }
}

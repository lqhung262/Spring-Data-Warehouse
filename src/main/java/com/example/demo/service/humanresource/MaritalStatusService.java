package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.MaritalStatus.MaritalStatusRequest;
import com.example.demo.dto.humanresource.MaritalStatus.MaritalStatusResponse;
import com.example.demo.entity.humanresource.MaritalStatus;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.MaritalStatusMapper;
import com.example.demo.repository.humanresource.MaritalStatusRepository;
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
public class MaritalStatusService {
    final MaritalStatusRepository maritalStatusRepository;
    final MaritalStatusMapper maritalStatusMapper;

    @Value("${entities.humanresource.maritalstatus}")
    private String entityName;


    public MaritalStatusResponse createMaritalStatus(MaritalStatusRequest request) {
        MaritalStatus maritalStatus = maritalStatusMapper.toMaritalStatus(request);

        return maritalStatusMapper.toMaritalStatusResponse(maritalStatusRepository.save(maritalStatus));
    }

    public List<MaritalStatusResponse> getMaritalStatuses(Pageable pageable) {
        Page<MaritalStatus> page = maritalStatusRepository.findAll(pageable);
        List<MaritalStatusResponse> dtos = page.getContent()
                .stream().map(maritalStatusMapper::toMaritalStatusResponse).toList();
        return dtos;
    }

    public MaritalStatusResponse getMaritalStatus(Long id) {
        return maritalStatusMapper.toMaritalStatusResponse(maritalStatusRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public MaritalStatusResponse updateMaritalStatus(Long id, MaritalStatusRequest request) {
        MaritalStatus maritalStatus = maritalStatusRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        maritalStatusMapper.updateMaritalStatus(maritalStatus, request);

        return maritalStatusMapper.toMaritalStatusResponse(maritalStatusRepository.save(maritalStatus));
    }

    public void deleteMaritalStatus(Long id) {
        MaritalStatus maritalStatus = maritalStatusRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        maritalStatusRepository.deleteById(id);
    }
}

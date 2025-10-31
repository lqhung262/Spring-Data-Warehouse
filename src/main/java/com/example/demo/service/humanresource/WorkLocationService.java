package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.WorkLocation.WorkLocationRequest;
import com.example.demo.dto.humanresource.WorkLocation.WorkLocationResponse;
import com.example.demo.entity.humanresource.WorkLocation;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.WorkLocationMapper;
import com.example.demo.repository.humanresource.WorkLocationRepository;
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
public class WorkLocationService {
    final WorkLocationRepository workLocationRepository;
    final WorkLocationMapper workLocationMapper;

    @Value("${entities.humanresource.worklocation}")
    private String entityName;


    public WorkLocationResponse createWorkLocation(WorkLocationRequest request) {
        WorkLocation workLocation = workLocationMapper.toWorkLocation(request);

        return workLocationMapper.toWorkLocationResponse(workLocationRepository.save(workLocation));
    }

    public List<WorkLocationResponse> getWorkLocations(Pageable pageable) {
        Page<WorkLocation> page = workLocationRepository.findAll(pageable);
        List<WorkLocationResponse> dtos = page.getContent()
                .stream().map(workLocationMapper::toWorkLocationResponse).toList();
        return dtos;
    }

    public WorkLocationResponse getWorkLocation(Long id) {
        return workLocationMapper.toWorkLocationResponse(workLocationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public WorkLocationResponse updateWorkLocation(Long id, WorkLocationRequest request) {
        WorkLocation workLocation = workLocationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        workLocationMapper.updateWorkLocation(workLocation, request);

        return workLocationMapper.toWorkLocationResponse(workLocationRepository.save(workLocation));
    }

    public void deleteWorkLocation(Long id) {
        WorkLocation workLocation = workLocationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        workLocationRepository.deleteById(id);
    }
}

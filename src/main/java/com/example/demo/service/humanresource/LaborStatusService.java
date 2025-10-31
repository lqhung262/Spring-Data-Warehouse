package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.LaborStatus.LaborStatusRequest;
import com.example.demo.dto.humanresource.LaborStatus.LaborStatusResponse;
import com.example.demo.entity.humanresource.LaborStatus;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.LaborStatusMapper;
import com.example.demo.repository.humanresource.LaborStatusRepository;
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
public class LaborStatusService {
    final LaborStatusRepository laborStatusRepository;
    final LaborStatusMapper laborStatusMapper;

    @Value("${entities.humanresource.laborstatus}")
    private String entityName;

    public LaborStatusResponse createLaborStatus(LaborStatusRequest request) {
        LaborStatus laborStatus = laborStatusMapper.toLaborStatus(request);

        return laborStatusMapper.toLaborStatusResponse(laborStatusRepository.save(laborStatus));
    }

    public List<LaborStatusResponse> getLaborStatuss(Pageable pageable) {
        Page<LaborStatus> page = laborStatusRepository.findAll(pageable);
        List<LaborStatusResponse> dtos = page.getContent()
                .stream().map(laborStatusMapper::toLaborStatusResponse).toList();
        return dtos;
    }

    public LaborStatusResponse getLaborStatus(Long id) {
        return laborStatusMapper.toLaborStatusResponse(laborStatusRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public LaborStatusResponse updateLaborStatus(Long id, LaborStatusRequest request) {
        LaborStatus laborStatus = laborStatusRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        laborStatusMapper.updateLaborStatus(laborStatus, request);

        return laborStatusMapper.toLaborStatusResponse(laborStatusRepository.save(laborStatus));
    }

    public void deleteLaborStatus(Long id) {
        LaborStatus laborStatus = laborStatusRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        laborStatusRepository.deleteById(id);
    }
}

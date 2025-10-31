package com.example.demo.service.general;

import com.example.demo.dto.general.SourceSystem.SourceSystemRequest;
import com.example.demo.dto.general.SourceSystem.SourceSystemResponse;
import com.example.demo.entity.general.SourceSystem;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.general.SourceSystemMapper;
import com.example.demo.repository.general.SourceSystemRepository;
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
public class SourceSystemService {
    final SourceSystemRepository sourceSystemRepository;
    final SourceSystemMapper sourceSystemMapper;

    @Value("${entities.general.sourcesystem}")
    private String entityName;


    public SourceSystemResponse createSourceSystem(SourceSystemRequest request) {
        SourceSystem sourceSystem = sourceSystemMapper.toSourceSystem(request);

        return sourceSystemMapper.toSourceSystemResponse(sourceSystemRepository.save(sourceSystem));
    }

    public List<SourceSystemResponse> getSourceSystems(Pageable pageable) {
        Page<SourceSystem> page = sourceSystemRepository.findAll(pageable);
        List<SourceSystemResponse> dtos = page.getContent()
                .stream().map(sourceSystemMapper::toSourceSystemResponse).toList();
        return dtos;
    }

    public SourceSystemResponse getSourceSystem(Long id) {
        return sourceSystemMapper.toSourceSystemResponse(sourceSystemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public SourceSystemResponse updateSourceSystem(Long id, SourceSystemRequest request) {
        SourceSystem sourceSystem = sourceSystemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        sourceSystemMapper.updateSourceSystem(sourceSystem, request);

        return sourceSystemMapper.toSourceSystemResponse(sourceSystemRepository.save(sourceSystem));
    }

    public void deleteSourceSystem(Long id) {
        SourceSystem sourceSystem = sourceSystemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        sourceSystemRepository.deleteById(id);
    }
}

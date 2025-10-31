package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.OldWard.OldWardRequest;
import com.example.demo.dto.humanresource.OldWard.OldWardResponse;
import com.example.demo.entity.humanresource.OldWard;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.OldWardMapper;
import com.example.demo.repository.humanresource.OldWardRepository;
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
public class OldWardService {
    final OldWardRepository oldWardRepository;
    final OldWardMapper oldWardMapper;

    @Value("${entities.humanresource.oldward}")
    private String entityName;

    public OldWardResponse createOldWard(OldWardRequest request) {
        OldWard oldWard = oldWardMapper.toOldWard(request);

        return oldWardMapper.toOldWardResponse(oldWardRepository.save(oldWard));
    }

    public List<OldWardResponse> getOldWards(Pageable pageable) {
        Page<OldWard> page = oldWardRepository.findAll(pageable);
        List<OldWardResponse> dtos = page.getContent()
                .stream().map(oldWardMapper::toOldWardResponse).toList();
        return dtos;
    }

    public OldWardResponse getOldWard(Long id) {
        return oldWardMapper.toOldWardResponse(oldWardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public OldWardResponse updateOldWard(Long id, OldWardRequest request) {
        OldWard oldWard = oldWardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        oldWardMapper.updateOldWard(oldWard, request);

        return oldWardMapper.toOldWardResponse(oldWardRepository.save(oldWard));
    }

    public void deleteOldWard(Long id) {
        OldWard oldWard = oldWardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        oldWardRepository.deleteById(id);
    }
}

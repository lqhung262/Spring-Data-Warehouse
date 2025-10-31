package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.Ward.WardRequest;
import com.example.demo.dto.humanresource.Ward.WardResponse;
import com.example.demo.entity.humanresource.Ward;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.WardMapper;
import com.example.demo.repository.humanresource.WardRepository;
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
public class WardService {
    final WardRepository WardRepository;
    final WardMapper WardMapper;

    @Value("${entities.humanresource.ward}")
    private String entityName;

    public WardResponse createWard(WardRequest request) {
        Ward Ward = WardMapper.toWard(request);

        return WardMapper.toWardResponse(WardRepository.save(Ward));
    }

    public List<WardResponse> getWards(Pageable pageable) {
        Page<Ward> page = WardRepository.findAll(pageable);
        List<WardResponse> dtos = page.getContent()
                .stream().map(WardMapper::toWardResponse).toList();
        return dtos;
    }

    public WardResponse getWard(Long id) {
        return WardMapper.toWardResponse(WardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public WardResponse updateWard(Long id, WardRequest request) {
        Ward Ward = WardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        WardMapper.updateWard(Ward, request);

        return WardMapper.toWardResponse(WardRepository.save(Ward));
    }

    public void deleteWard(Long id) {
        Ward Ward = WardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        WardRepository.deleteById(id);
    }
}

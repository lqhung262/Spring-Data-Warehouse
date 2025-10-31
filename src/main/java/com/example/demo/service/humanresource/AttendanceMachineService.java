package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.AttendanceMachine.AttendanceMachineRequest;
import com.example.demo.dto.humanresource.AttendanceMachine.AttendanceMachineResponse;
import com.example.demo.entity.humanresource.AttendanceMachine;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.AttendanceMachineMapper;
import com.example.demo.repository.humanresource.AttendanceMachineRepository;
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
public class AttendanceMachineService {
    final AttendanceMachineRepository attendanceMachineRepository;
    final AttendanceMachineMapper attendanceMachineMapper;

    @Value("${entities.humanresource.attendancemachine}")
    private String entityName;


    public AttendanceMachineResponse createAttendanceMachine(AttendanceMachineRequest request) {
        AttendanceMachine attendanceMachine = attendanceMachineMapper.toAttendanceMachine(request);

        return attendanceMachineMapper.toAttendanceMachineResponse(attendanceMachineRepository.save(attendanceMachine));
    }

    public List<AttendanceMachineResponse> getAttendanceMachines(Pageable pageable) {
        Page<AttendanceMachine> page = attendanceMachineRepository.findAll(pageable);
        List<AttendanceMachineResponse> dtos = page.getContent()
                .stream().map(attendanceMachineMapper::toAttendanceMachineResponse).toList();
        return dtos;
    }

    public AttendanceMachineResponse getAttendanceMachine(Long id) {
        return attendanceMachineMapper.toAttendanceMachineResponse(attendanceMachineRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public AttendanceMachineResponse updateAttendanceMachine(Long id, AttendanceMachineRequest request) {
        AttendanceMachine attendanceMachine = attendanceMachineRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        attendanceMachineMapper.updateAttendanceMachine(attendanceMachine, request);

        return attendanceMachineMapper.toAttendanceMachineResponse(attendanceMachineRepository.save(attendanceMachine));
    }

    public void deleteAttendanceMachine(Long id) {
        AttendanceMachine attendanceMachine = attendanceMachineRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        attendanceMachineRepository.deleteById(id);
    }
}

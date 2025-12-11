package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.AttendanceMachine.AttendanceMachineRequest;
import com.example.demo.dto.humanresource.AttendanceMachine.AttendanceMachineResponse;
import com.example.demo.dto.kafka.JobSubmissionResponse;
import com.example.demo.kafka.enums.DataDomain;
import com.example.demo.kafka.enums.MessageSpec;
import com.example.demo.kafka.enums.OperationType;
import com.example.demo.kafka.producer.KafkaProducerService;
import com.example.demo.kafka.service.KafkaJobStatusService;
import com.example.demo.service.humanresource.AttendanceMachineService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/human-resource/attendance-machines")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AttendanceMachineController {
    AttendanceMachineService attendanceMachineService;
    final KafkaProducerService kafkaProducerService;
    final KafkaJobStatusService jobStatusService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<AttendanceMachineResponse> createAttendanceMachine(@Valid @RequestBody AttendanceMachineRequest request) {
        ApiResponse<AttendanceMachineResponse> response = new ApiResponse<>();

        response.setResult(attendanceMachineService.createAttendanceMachine(request));

        return response;
    }

    /**
     * BULK UPSERT ENDPOINT
     */
    @PostMapping("/bulk-upsert")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResponse<JobSubmissionResponse> bulkUpsertAttendanceMachines(
            @RequestBody List<AttendanceMachineRequest> requests) {
        log.info("Received bulk upsert request for {} attendance machines", requests.size());

        // Create job
        String jobId = jobStatusService.createJob("ATTENDANCE_MACHINE", OperationType.UPSERT, requests.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, requests, MessageSpec.ATTENDANCE_MACHINE_UPSERT, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "ATTENDANCE_MACHINE", OperationType.UPSERT, requests.size());

        return ApiResponse.<JobSubmissionResponse>builder()
                .code(HttpStatus.ACCEPTED.value())
                .message("Bulk upsert request accepted")
                .result(response)
                .build();
    }

    /**
     * BULK DELETE ENDPOINT
     */
    @DeleteMapping("/bulk-delete")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResponse<JobSubmissionResponse> bulkDeleteAttendanceMachines(@RequestBody List<Long> ids) {
        log.info("Received bulk delete request for {} attendance machines", ids.size());

        // Create job
        String jobId = jobStatusService.createJob("ATTENDANCE_MACHINE", OperationType.DELETE, ids.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, ids, MessageSpec.ATTENDANCE_MACHINE_DELETE, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "ATTENDANCE_MACHINE", OperationType.DELETE, ids.size());

        return ApiResponse.<JobSubmissionResponse>builder()
                .code(HttpStatus.ACCEPTED.value())
                .message("Bulk delete request accepted")
                .result(response)
                .build();
    }


    @GetMapping()
    ApiResponse<List<AttendanceMachineResponse>> getAttendanceMachines(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                                       @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                                       @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                                       @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<AttendanceMachineResponse>>builder()
                .result(attendanceMachineService.getAttendanceMachines(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{attendanceMachineId}")
    ApiResponse<AttendanceMachineResponse> getAttendanceMachine(@PathVariable("attendanceMachineId") Long attendanceMachineId) {
        return ApiResponse.<AttendanceMachineResponse>builder()
                .result(attendanceMachineService.getAttendanceMachine(attendanceMachineId))
                .build();
    }

    @PutMapping("/{attendanceMachineId}")
    ApiResponse<AttendanceMachineResponse> updateAttendanceMachine(@PathVariable("attendanceMachineId") Long attendanceMachineId, @RequestBody AttendanceMachineRequest request) {
        return ApiResponse.<AttendanceMachineResponse>builder()
                .result(attendanceMachineService.updateAttendanceMachine(attendanceMachineId, request))
                .build();
    }

    @DeleteMapping("/{attendanceMachineId}")
    ApiResponse<String> deleteAttendanceMachine(@PathVariable Long attendanceMachineId) {
        attendanceMachineService.deleteAttendanceMachine(attendanceMachineId);
        return ApiResponse.<String>builder().result("Attendance Machine has been deleted").build();
    }
}

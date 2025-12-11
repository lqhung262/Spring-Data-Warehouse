package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.AttendanceType.AttendanceTypeRequest;
import com.example.demo.dto.humanresource.AttendanceType.AttendanceTypeResponse;
import com.example.demo.dto.kafka.JobSubmissionResponse;
import com.example.demo.kafka.enums.DataDomain;
import com.example.demo.kafka.enums.MessageSpec;
import com.example.demo.kafka.enums.OperationType;
import com.example.demo.kafka.producer.KafkaProducerService;
import com.example.demo.kafka.service.KafkaJobStatusService;
import com.example.demo.service.humanresource.AttendanceTypeService;
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
@RequestMapping("/api/v1/human-resource/attendance-types")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AttendanceTypeController {
    AttendanceTypeService attendanceTypeService;
    final KafkaProducerService kafkaProducerService;
    final KafkaJobStatusService jobStatusService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<AttendanceTypeResponse> createAttendanceType(@Valid @RequestBody AttendanceTypeRequest request) {
        ApiResponse<AttendanceTypeResponse> response = new ApiResponse<>();

        response.setResult(attendanceTypeService.createAttendanceType(request));

        return response;
    }

    /**
     * BULK UPSERT ENDPOINT
     */
    @PostMapping("/bulk-upsert")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResponse<JobSubmissionResponse> bulkUpsertAttendanceTypes(
            @RequestBody List<AttendanceTypeRequest> requests) {
        log.info("Received bulk upsert request for {} attendance types", requests.size());

        // Create job
        String jobId = jobStatusService.createJob("ATTENDANCE_TYPE", OperationType.UPSERT, requests.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, requests, MessageSpec.ATTENDANCE_TYPE_UPSERT, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "ATTENDANCE_TYPE", OperationType.UPSERT, requests.size());

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
    public ApiResponse<JobSubmissionResponse> bulkDeleteAttendanceTypes(@RequestBody List<Long> ids) {
        log.info("Received bulk delete request for {} attendance types", ids.size());

        // Create job
        String jobId = jobStatusService.createJob("ATTENDANCE_TYPE", OperationType.DELETE, ids.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, ids, MessageSpec.ATTENDANCE_TYPE_DELETE, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "ATTENDANCE_TYPE", OperationType.DELETE, ids.size());

        return ApiResponse.<JobSubmissionResponse>builder()
                .code(HttpStatus.ACCEPTED.value())
                .message("Bulk delete request accepted")
                .result(response)
                .build();
    }

    @GetMapping()
    ApiResponse<List<AttendanceTypeResponse>> getAttendanceTypes(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                                 @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                                 @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                                 @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<AttendanceTypeResponse>>builder()
                .result(attendanceTypeService.getAttendanceTypes(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{attendanceTypeId}")
    ApiResponse<AttendanceTypeResponse> getAttendanceType(@PathVariable("attendanceTypeId") Long attendanceTypeId) {
        return ApiResponse.<AttendanceTypeResponse>builder()
                .result(attendanceTypeService.getAttendanceType(attendanceTypeId))
                .build();
    }

    @PutMapping("/{attendanceTypeId}")
    ApiResponse<AttendanceTypeResponse> updateAttendanceType(@PathVariable("attendanceTypeId") Long attendanceTypeId, @RequestBody AttendanceTypeRequest request) {
        return ApiResponse.<AttendanceTypeResponse>builder()
                .result(attendanceTypeService.updateAttendanceType(attendanceTypeId, request))
                .build();
    }

    @DeleteMapping("/{attendanceTypeId}")
    ApiResponse<String> deleteAttendanceType(@PathVariable Long attendanceTypeId) {
        attendanceTypeService.deleteAttendanceType(attendanceTypeId);
        return ApiResponse.<String>builder().result("Attendance Type  has been deleted").build();
    }
}

package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.AttendanceType.AttendanceTypeRequest;
import com.example.demo.dto.humanresource.AttendanceType.AttendanceTypeResponse;
import com.example.demo.service.humanresource.AttendanceTypeService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/attendance-types")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AttendanceTypeController {
    AttendanceTypeService attendanceTypeService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<AttendanceTypeResponse> createAttendanceType(@Valid @RequestBody AttendanceTypeRequest request) {
        ApiResponse<AttendanceTypeResponse> response = new ApiResponse<>();

        response.setResult(attendanceTypeService.createAttendanceType(request));

        return response;
    }

//    @PostMapping("/_bulk-upsert")
//    ApiResponse<List<AttendanceTypeResponse>> bulkAttendanceTypeUpsert(@Valid @RequestBody List<AttendanceTypeRequest> requests) {
//        return ApiResponse.<List<AttendanceTypeResponse>>builder()
//                .result(attendanceTypeService.bulkUpsertAttendanceTypes(requests))
//                .build();
//    }
//
//    @DeleteMapping("/_bulk-delete")
//    public ApiResponse<String> bulkDeleteAttendanceTypes(@Valid @RequestParam("ids") List<Long> attendanceTypeIds) {
//        attendanceTypeService.bulkDeleteAttendanceTypes(attendanceTypeIds);
//        return ApiResponse.<String>builder()
//                .result(attendanceTypeIds.size() + " attendanceTypes have been deleted.")
//                .build();
//    }


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

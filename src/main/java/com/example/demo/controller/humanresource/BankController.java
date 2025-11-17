package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.Bank.BankRequest;
import com.example.demo.dto.humanresource.Bank.BankResponse;
import com.example.demo.service.humanresource.BankService;
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
@RequestMapping("/banks")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BankController {
    BankService bankService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
        // 201 Created
    ApiResponse<BankResponse> createBank(@Valid @RequestBody BankRequest request) {
        ApiResponse<BankResponse> response = new ApiResponse<>();

        response.setResult(bankService.createBank(request));

        return response;
    }

//    @PostMapping("/_bulk-upsert")
//    ApiResponse<List<BankResponse>> bulkBankUpsert(@Valid @RequestBody List<BankRequest> requests) {
//        return ApiResponse.<List<BankResponse>>builder()
//                .result(bankService.bulkUpsertBanks(requests))
//                .build();
//    }
//
//    @DeleteMapping("/_bulk-delete")
//    public ApiResponse<String> bulkDeleteBanks(@Valid @RequestParam("ids") List<Long> bankIds) {
//        bankService.bulkDeleteBanks(bankIds);
//        return ApiResponse.<String>builder()
//                .result(bankIds.size() + " banks have been deleted.")
//                .build();
//    }

    @GetMapping()
    ApiResponse<List<BankResponse>> getBanks(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                             @RequestParam(required = false, defaultValue = "5") int pageSize,
                                             @RequestParam(required = false, defaultValue = "name") String sortBy,
                                             @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<BankResponse>>builder()
                .result(bankService.getBanks(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{bankId}")
    ApiResponse<BankResponse> getBank(@PathVariable("bankId") Long bankId) {
        return ApiResponse.<BankResponse>builder()
                .result(bankService.getBank(bankId))
                .build();
    }

    @PutMapping("/{bankId}")
    ApiResponse<BankResponse> updateBank(@PathVariable("bankId") Long bankId, @RequestBody BankRequest request) {
        return ApiResponse.<BankResponse>builder()
                .result(bankService.updateBank(bankId, request))
                .build();
    }

    @DeleteMapping("/{bankId}")
    ApiResponse<String> deleteBank(@PathVariable Long bankId) {
        bankService.deleteBank(bankId);
        return ApiResponse.<String>builder().result("Bank has been deleted").build();
    }
}

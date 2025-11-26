package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.Bank.BankRequest;
import com.example.demo.dto.humanresource.Bank.BankResponse;
import com.example.demo.entity.humanresource.Bank;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.BankMapper;
import com.example.demo.repository.humanresource.BankRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.demo.repository.humanresource.EmployeeRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BankService {
    final BankRepository bankRepository;
    final BankMapper bankMapper;
    final EmployeeRepository employeeRepository;

    @Value("${entities.humanresource.bank}")
    private String entityName;

    public BankResponse createBank(BankRequest request) {
        // Check source_id uniqueness for create
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            bankRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        Bank bank = bankMapper.toBank(request);

        return bankMapper.toBankResponse(bankRepository.save(bank));
    }

//    /**
//     * Xử lý Bulk Upsert
//     */
//    @Transactional
//    public List<BankResponse> bulkUpsertBanks(List<BankRequest> requests) {
//
//        // Lấy tất cả bankCodes từ request
//        List<String> bankCodes = requests.stream()
//                .map(BankRequest::getBankCode)
//                .toList();
//
//        // Tìm tất cả các bank đã tồn tại TRONG 1 CÂU QUERY
//        Map<String, Bank> existingBanksMap = bankRepository.findByBankCodeIn(bankCodes).stream()
//                .collect(Collectors.toMap(Bank::getBankCode, bank -> bank));
//
//        List<Bank> banksToSave = new java.util.ArrayList<>();
//
//        // Lặp qua danh sách request để quyết định UPDATE hay INSERT
//        for (BankRequest request : requests) {
//            Bank bank = existingBanksMap.get(request.getBankCode());
//
//            if (bank != null) {
//                // --- Logic UPDATE ---
//                // Bank đã tồn tại -> Cập nhật
//                bankMapper.updateBank(bank, request);
//                banksToSave.add(bank);
//            } else {
//                // --- Logic INSERT ---
//                // Bank chưa tồn tại -> Tạo mới
//                Bank newBank = bankMapper.toBank(request);
//                banksToSave.add(newBank);
//            }
//        }
//
//        // Lưu tất cả (cả insert và update) TRONG 1 LỆNH
//        List<Bank> savedBanks = bankRepository.saveAll(banksToSave);
//
//        // Map sang Response DTO và trả về
//        return savedBanks.stream()
//                .map(bankMapper::toBankResponse)
//                .toList();
//    }
//
//    /**
//     * Xử lý Bulk Delete
//     */
//    @Transactional
//    public void bulkDeleteBanks(List<Long> ids) {
//        // Kiểm tra xem có bao nhiêu ID tồn tại
//        long existingCount = bankRepository.countByBankIdIn(ids);
//        if (existingCount != ids.size()) {
//            // Không phải tất cả ID đều tồn tại
//            throw new NotFoundException("Some" + entityName + "s not found. Cannot complete bulk delete.");
//        }
//
//        // Xóa tất cả bằng ID trong 1 câu query (hiệu quả)
//        bankRepository.deleteAllById(ids);
//    }


    public List<BankResponse> getBanks(Pageable pageable) {
        Page<Bank> page = bankRepository.findAll(pageable);
        return page.getContent()
                .stream().map(bankMapper::toBankResponse).toList();
    }

    public BankResponse getBank(Long id) {
        return bankMapper.toBankResponse(bankRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public BankResponse updateBank(Long id, BankRequest request) {
        Bank bank = bankRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            bankRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getBankId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        bankMapper.updateBank(bank, request);

        return bankMapper.toBankResponse(bankRepository.save(bank));
    }

    public void deleteBank(Long id) {
        if (!bankRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        // Check references (RESTRICT strategy)
        long refCount = employeeRepository.countByBank_BankId(id);
        if (refCount > 0) {
            throw new com.example.demo.exception.CannotDeleteException(
                    "Bank", id, "Employee", refCount
            );
        }

        bankRepository.deleteById(id);
    }
}

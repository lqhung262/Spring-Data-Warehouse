package com.example.demo.service.humanresource;

import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.humanresource.Bank.BankRequest;
import com.example.demo.dto.humanresource.Bank.BankResponse;
import com.example.demo.entity.humanresource.Bank;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.BankMapper;
import com.example.demo.repository.humanresource.BankRepository;
import com.example.demo.util.bulk.*;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.demo.repository.humanresource.EmployeeRepository;

import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BankService {
    final BankRepository bankRepository;
    final BankMapper bankMapper;
    final EmployeeRepository employeeRepository;
    final EntityManager entityManager;

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

    /**
     * Bulk Upsert với Final Batch Logic, Partial Success Pattern:
     * - Safe Batch: saveAll() - requests không có unique conflicts
     * - Final Batch: save + flush từng request - requests có potential conflicts
     * - Trả về detailed result với success/failure breakdown
     */
    public BulkOperationResult<BankResponse> bulkUpsertBanks(
            List<BankRequest> requests) {

        // 1. Define unique field configurations (Bank has 2 unique fields)
        UniqueFieldConfig<BankRequest> sourceIdConfig =
                new UniqueFieldConfig<>("source_id", BankRequest::getSourceId);
        UniqueFieldConfig<BankRequest> codeConfig =
                new UniqueFieldConfig<>("bank_code", BankRequest::getBankCode);
        UniqueFieldConfig<BankRequest> nameConfig =
                new UniqueFieldConfig<>("name", BankRequest::getName);

        // 2. Define entity fetchers for each unique field
        Map<String, Function<Set<String>, List<Bank>>> entityFetchers = new HashMap<>();
        entityFetchers.put("source_id", bankRepository::findBySourceIdIn);
        entityFetchers.put("bank_code", bankRepository::findByBankCodeIn);
        entityFetchers.put("name", bankRepository::findByNameIn);

        // 2.5. Define entity field extractors
        Map<String, Function<Bank, String>> entityFieldExtractors = new HashMap<>();
        entityFieldExtractors.put("source_id", Bank::getSourceId);
        entityFieldExtractors.put("bank_code", Bank::getBankCode);
        entityFieldExtractors.put("name", Bank::getName);

        // 3. Setup unique fields using helper
        UniqueFieldsSetupHelper.UniqueFieldsSetup<BankRequest> setup =
                UniqueFieldsSetupHelper.buildUniqueFieldsSetup(
                        requests,
                        entityFetchers,
                        entityFieldExtractors,
                        sourceIdConfig,
                        codeConfig,
                        nameConfig
                );

        // 4.  Build bulk upsert config
        BulkUpsertConfig<BankRequest, Bank, BankResponse> config =
                BulkUpsertConfig.<BankRequest, Bank, BankResponse>builder()
                        .uniqueFieldExtractors(setup.getUniqueFieldExtractors())
                        .existingValuesMaps(setup.getExistingValuesMaps())
                        .entityToResponseMapper(bankMapper::toBankResponse)
                        .requestToEntityMapper(bankMapper::toBank)
                        .entityUpdater(bankMapper::updateBank)
                        .existingEntityFinder(this::findExistingEntityForUpsert)
                        .repositorySaver(bankRepository::saveAll)
                        .repositorySaveAndFlusher(bankRepository::saveAndFlush)
                        .entityManagerClearer(entityManager::clear)
                        .build();

        // 5. Execute bulk upsert
        BulkUpsertProcessor<BankRequest, Bank, BankResponse> processor =
                new BulkUpsertProcessor<>(config);

        return processor.execute(requests);
    }

    /**
     * Helper: Find existing entity
     */
    private Bank findExistingEntityForUpsert(BankRequest request) {
        if (request.getSourceId() != null && !request.getSourceId().trim().isEmpty()) {
            Optional<Bank> bySourceId = bankRepository.findBySourceId(request.getSourceId());
            if (bySourceId.isPresent()) {
                return bySourceId.get();
            }
        }

        return null;
    }

    // ========================= BULK DELETE  ========================

    public BulkOperationResult<Long> bulkDeleteBanks(List<Long> ids) {

        // Build config
        BulkDeleteConfig<Bank> config = BulkDeleteConfig.<Bank>builder()
                .entityFinder(id -> bankRepository.findById(id).orElse(null))
                .foreignKeyConstraintsChecker(this::checkForeignKeyConstraints)
                .repositoryDeleter(bankRepository::deleteById)
                .entityName(entityName)
                .build();

        // Execute với processor
        BulkDeleteProcessor<Bank> processor = new BulkDeleteProcessor<>(config);

        return processor.execute(ids);
    }


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
        checkForeignKeyConstraints(id);

        bankRepository.deleteById(id);
    }

    private void checkForeignKeyConstraints(Long id) {
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
    }
}

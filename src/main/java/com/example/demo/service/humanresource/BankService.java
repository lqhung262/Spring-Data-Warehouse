package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.Bank.BankRequest;
import com.example.demo.dto.humanresource.Bank.BankResponse;
import com.example.demo.entity.humanresource.Bank;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.BankMapper;
import com.example.demo.repository.humanresource.BankRepository;
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
public class BankService {
    final BankRepository bankRepository;
    final BankMapper bankMapper;

    @Value("${entities.humanresource.bank}")
    private String entityName;

    public BankResponse createBank(BankRequest request) {
        Bank bank = bankMapper.toBank(request);

        return bankMapper.toBankResponse(bankRepository.save(bank));
    }

    public List<BankResponse> getBanks(Pageable pageable) {
        Page<Bank> page = bankRepository.findAll(pageable);
        List<BankResponse> dtos = page.getContent()
                .stream().map(bankMapper::toBankResponse).toList();
        return dtos;
    }

    public BankResponse getBank(Long id) {
        return bankMapper.toBankResponse(bankRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public BankResponse updateBank(Long id, BankRequest request) {
        Bank bank = bankRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        bankMapper.updateBank(bank, request);

        return bankMapper.toBankResponse(bankRepository.save(bank));
    }

    public void deleteBank(Long id) {
        Bank bank = bankRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        bankRepository.deleteById(id);
    }
}

//package com.sujit.personal_Finance_Manager.service;
//
//import com.sujit.personal_Finance_Manager.dto.TransactionDTO;
//import com.sujit.personal_Finance_Manager.entity.*;
//import com.sujit.personal_Finance_Manager.exception.BadRequestException;
//import com.sujit.personal_Finance_Manager.repository.TransactionRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class TransactionService {
//    private final TransactionRepository transactionRepository;
//    private final CategoryService categoryService;
//
//    public Transaction create(User user, TransactionDTO dto) {
//        if (dto.getAmount() == null || dto.getAmount() <= 0)
//            throw new BadRequestException("Amount must be positive.");
//        LocalDate date = LocalDate.parse(dto.getDate());
//        if (date.isAfter(LocalDate.now()))
//            throw new BadRequestException("Date cannot be in the future.");
//        Category cat = categoryService.findValidCategory(user, dto.getCategory());
//        Transaction tx = Transaction.builder()
//                .amount(dto.getAmount())
//                .date(date)
//                .category(cat)
//                .description(dto.getDescription())
//                .user(user)
//                .build();
//        return transactionRepository.save(tx);
//    }
//
//    public List<Transaction> getAll(User user) {
//        return transactionRepository.findByUserOrderByDateDesc(user);
//    }
//
//    public List<Transaction> filter(User user, LocalDate start, LocalDate end, String categoryName) {
//        if (categoryName != null) {
//            Category cat = categoryService.findValidCategory(user, categoryName);
//            return transactionRepository.findByUserAndDateBetweenAndCategory(user, start, end, cat);
//        }
//        return transactionRepository.findByUserAndDateBetween(user, start, end);
//    }
//
//    public Transaction update(User user, Long id, Double amount, String description) {
//        Transaction tx = transactionRepository.findById(id)
//                .orElseThrow(() -> new BadRequestException("Transaction not found."));
//        if (!tx.getUser().getId().equals(user.getId()))
//            throw new BadRequestException("Forbidden");
//        if (amount != null && amount > 0)
//            tx.setAmount(amount);
//        if (description != null)
//            tx.setDescription(description);
//        return transactionRepository.save(tx);
//    }
//
//    public void delete(User user, Long id) {
//        Transaction tx = transactionRepository.findById(id)
//                .orElseThrow(() -> new BadRequestException("Transaction not found."));
//        if (!tx.getUser().getId().equals(user.getId()))
//            throw new BadRequestException("Forbidden");
//        transactionRepository.delete(tx);
//    }
//}

package com.sujit.personal_Finance_Manager.service;

import com.sujit.personal_Finance_Manager.dto.TransactionDTO;
import com.sujit.personal_Finance_Manager.entity.Category;
import com.sujit.personal_Finance_Manager.entity.Transaction;
import com.sujit.personal_Finance_Manager.entity.User;
import com.sujit.personal_Finance_Manager.exception.BadRequestException;
import com.sujit.personal_Finance_Manager.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final CategoryService categoryService;

    public Transaction create(User user, TransactionDTO dto) {
        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Amount must be positive.");
        }

        LocalDate date = LocalDate.parse(dto.getDate());
        if (date.isAfter(LocalDate.now())) {
            throw new BadRequestException("Date cannot be in the future.");
        }

        Category cat = categoryService.findValidCategory(user, dto.getCategory());

        Transaction tx = Transaction.builder()
                .amount(dto.getAmount())
                .date(date)
                .category(cat)
                .description(dto.getDescription())
                .user(user)
                .build();

        return transactionRepository.save(tx);
    }

    public List<Transaction> getAll(User user) {
        return transactionRepository.findByUserOrderByDateDesc(user);
    }

    public List<Transaction> filter(User user, LocalDate start, LocalDate end, String categoryName) {
        if (categoryName != null) {
            Category cat = categoryService.findValidCategory(user, categoryName);
            return transactionRepository.findByUserAndDateBetweenAndCategory(user, start, end, cat);
        }
        return transactionRepository.findByUserAndDateBetween(user, start, end);
    }

    public Transaction update(User user, Long id, BigDecimal amount, String description) {
        Transaction tx = transactionRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Transaction not found."));

        if (!tx.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Forbidden");
        }

        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            tx.setAmount(amount);
        }

        if (description != null) {
            tx.setDescription(description);
        }

        return transactionRepository.save(tx);
    }

    public void delete(User user, Long id) {
        Transaction tx = transactionRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Transaction not found."));

        if (!tx.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Forbidden");
        }

        transactionRepository.delete(tx);
    }
}

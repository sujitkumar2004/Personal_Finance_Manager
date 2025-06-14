//package com.sujit.personal_Finance_Manager.controller;
//
//import com.sujit.personal_Finance_Manager.dto.TransactionDTO;
//import com.sujit.personal_Finance_Manager.entity.Transaction;
//import com.sujit.personal_Finance_Manager.entity.User;
//import com.sujit.personal_Finance_Manager.service.TransactionService;
//import com.sujit.personal_Finance_Manager.service.UserService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.*;
//
//import java.security.Principal;
//import java.time.LocalDate;
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/transactions")
//@RequiredArgsConstructor
//public class TransactionController {
//    private final TransactionService transactionService;
//    private final UserService userService;
//
//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public Transaction create(Principal principal, @RequestBody TransactionDTO dto) {
//        User user = userService.findByUsername(principal.getName()).orElseThrow();
//        return transactionService.create(user, dto);
//    }
//
//    @GetMapping
//    public List<Transaction> getAll(
//            Principal principal,
//            @RequestParam(required = false) String startDate,
//            @RequestParam(required = false) String endDate,
//            @RequestParam(required = false) String category
//    ) {
//        User user = userService.findByUsername(principal.getName()).orElseThrow();
//        if (startDate != null && endDate != null) {
//            LocalDate start = LocalDate.parse(startDate);
//            LocalDate end = LocalDate.parse(endDate);
//            return transactionService.filter(user, start, end, category);
//        }
//        return transactionService.getAll(user);
//    }
//
//    @PutMapping("/{id}")
//    public Transaction update(Principal principal, @PathVariable Long id, @RequestBody TransactionDTO dto) {
//        User user = userService.findByUsername(principal.getName()).orElseThrow();
//        return transactionService.update(user, id, dto.getAmount(), dto.getDescription());
//    }
//
//    @DeleteMapping("/{id}")
//    public void delete(Principal principal, @PathVariable Long id) {
//        User user = userService.findByUsername(principal.getName()).orElseThrow();
//        transactionService.delete(user, id);
//    }
//}

package com.sujit.personal_Finance_Manager.controller;

import com.sujit.personal_Finance_Manager.dto.TransactionDTO;
import com.sujit.personal_Finance_Manager.entity.Transaction;
import com.sujit.personal_Finance_Manager.entity.User;
import com.sujit.personal_Finance_Manager.service.TransactionService;
import com.sujit.personal_Finance_Manager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionDTO create(Principal principal, @RequestBody TransactionDTO dto) {
        User user = userService.findByUsername(principal.getName()).orElseThrow();
        Transaction tx = transactionService.create(user, dto);
        return toDTO(tx);
    }

    @GetMapping
    public List<TransactionDTO> getAll(
            Principal principal,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String category
    ) {
        User user = userService.findByUsername(principal.getName()).orElseThrow();
        //changes
        System.out.println("🔍 Fetching transactions for user: " + user.getUsername());

        List<Transaction> transactions;
        if (startDate != null && endDate != null) {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            transactions = transactionService.filter(user, start, end, category);
        } else {
            transactions = transactionService.getAll(user);
        }
        return transactions.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @PutMapping("/{id}")
    public TransactionDTO update(Principal principal, @PathVariable Long id, @RequestBody TransactionDTO dto) {
        User user = userService.findByUsername(principal.getName()).orElseThrow();
        Transaction tx = transactionService.update(user, id, dto.getAmount(), dto.getDescription());
        return toDTO(tx);
    }

    @DeleteMapping("/{id}")
    public void delete(Principal principal, @PathVariable Long id) {
        User user = userService.findByUsername(principal.getName()).orElseThrow();
        transactionService.delete(user, id);
    }

    private TransactionDTO toDTO(Transaction tx) {
        return TransactionDTO.builder()
                .id(tx.getId())
                .amount(tx.getAmount())
                .date(tx.getDate().toString())
                .description(tx.getDescription())
                .category(tx.getCategory().getName())
                .type(tx.getCategory().getType().name()) // INCOME or EXPENSE
                .build();
    }
}

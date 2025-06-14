//package com.sujit.personal_Finance_Manager.service;
//
//import com.sujit.personal_Finance_Manager.dto.ReportDTO;
//import com.sujit.personal_Finance_Manager.entity.*;
//import com.sujit.personal_Finance_Manager.repository.TransactionRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.util.*;
//
//@Service
//@RequiredArgsConstructor
//public class ReportService {
//    private final TransactionRepository transactionRepository;
//
//    public ReportDTO getMonthlyReport(User user, int year, int month) {
//        LocalDate start = LocalDate.of(year, month, 1);
//        LocalDate end = start.plusMonths(1).minusDays(1);
//
//        List<Transaction> txs = transactionRepository.findByUserAndDateBetween(user, start, end);
//
//        Map<String, Double> income = new HashMap<>();
//        Map<String, Double> expense = new HashMap<>();
//        double netSavings = 0.0;
//
//        for (Transaction tx : txs) {
//            String cat = tx.getCategory().getName();
//            if (tx.getCategory().getType() == CategoryType.INCOME) {
//                income.put(cat, income.getOrDefault(cat, 0.0) + tx.getAmount());
//                netSavings += tx.getAmount();
//            } else {
//                expense.put(cat, expense.getOrDefault(cat, 0.0) + tx.getAmount());
//                netSavings -= tx.getAmount();
//            }
//        }
//
//        return ReportDTO.builder()
//                .month(month)
//                .year(year)
//                .totalIncome(income)
//                .totalExpenses(expense)
//                .netSavings(netSavings)
//                .build();
//    }
//
//    public ReportDTO getYearlyReport(User user, int year) {
//        Map<String, Double> totalIncome = new HashMap<>();
//        Map<String, Double> totalExpenses = new HashMap<>();
//        double netSavings = 0.0;
//
//        for (int m = 1; m <= 12; m++) {
//            ReportDTO monthly = getMonthlyReport(user, year, m);
//            monthly.getTotalIncome().forEach((cat, amt) -> totalIncome.put(cat, totalIncome.getOrDefault(cat, 0.0) + amt));
//            monthly.getTotalExpenses().forEach((cat, amt) -> totalExpenses.put(cat, totalExpenses.getOrDefault(cat, 0.0) + amt));
//            netSavings += monthly.getNetSavings();
//        }
//
//        return ReportDTO.builder()
//                .month(0)
//                .year(year)
//                .totalIncome(totalIncome)
//                .totalExpenses(totalExpenses)
//                .netSavings(netSavings)
//                .build();
//    }
//}

//big decimal compatible
package com.sujit.personal_Finance_Manager.service;

import com.sujit.personal_Finance_Manager.dto.ReportDTO;
import com.sujit.personal_Finance_Manager.entity.*;
import com.sujit.personal_Finance_Manager.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final TransactionRepository transactionRepository;

    public ReportDTO getMonthlyReport(User user, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1).minusDays(1);

        List<Transaction> txs = transactionRepository.findByUserAndDateBetween(user, start, end);

        Map<String, BigDecimal> income = new HashMap<>();
        Map<String, BigDecimal> expense = new HashMap<>();
        BigDecimal netSavings = BigDecimal.ZERO;

        for (Transaction tx : txs) {
            String cat = tx.getCategory().getName();
            BigDecimal amount = tx.getAmount();

            if (tx.getCategory().getType() == CategoryType.INCOME) {
                income.put(cat, income.getOrDefault(cat, BigDecimal.ZERO).add(amount));
                netSavings = netSavings.add(amount);
            } else {
                expense.put(cat, expense.getOrDefault(cat, BigDecimal.ZERO).add(amount));
                netSavings = netSavings.subtract(amount);
            }
        }

        return ReportDTO.builder()
                .month(month)
                .year(year)
                .totalIncome(income)
                .totalExpenses(expense)
                .netSavings(netSavings)
                .build();
    }

    public ReportDTO getYearlyReport(User user, int year) {
        Map<String, BigDecimal> totalIncome = new HashMap<>();
        Map<String, BigDecimal> totalExpenses = new HashMap<>();
        BigDecimal netSavings = BigDecimal.ZERO;

        for (int m = 1; m <= 12; m++) {
            ReportDTO monthly = getMonthlyReport(user, year, m);

            monthly.getTotalIncome().forEach((cat, amt) ->
                    totalIncome.put(cat, totalIncome.getOrDefault(cat, BigDecimal.ZERO).add(amt))
            );

            monthly.getTotalExpenses().forEach((cat, amt) ->
                    totalExpenses.put(cat, totalExpenses.getOrDefault(cat, BigDecimal.ZERO).add(amt))
            );

            netSavings = netSavings.add(monthly.getNetSavings());
        }

        return ReportDTO.builder()
                .month(0)
                .year(year)
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netSavings(netSavings)
                .build();
    }
}

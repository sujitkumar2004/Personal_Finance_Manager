//package com.sujit.personal_Finance_Manager.service;
//
//import com.sujit.personal_Finance_Manager.dto.SavingsGoalDTO;
//import com.sujit.personal_Finance_Manager.entity.SavingsGoal;
//import com.sujit.personal_Finance_Manager.entity.User;
//import com.sujit.personal_Finance_Manager.exception.BadRequestException;
//import com.sujit.personal_Finance_Manager.repository.SavingsGoalRepository;
//import com.sujit.personal_Finance_Manager.repository.TransactionRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class SavingsGoalService {
//    private final SavingsGoalRepository savingsGoalRepository;
//    private final TransactionRepository transactionRepository;
//
//
//
////    public SavingsGoal create(User user, SavingsGoalDTO dto) {
////
////        SavingsGoal goal = SavingsGoal.builder()
////                .goalName(dto.getGoalName())
////                .targetAmount(dto.getTargetAmount())
////                .startDate(LocalDate.parse(dto.getStartDate()))
////                .targetDate(LocalDate.parse(dto.getTargetDate()))
////                .user(user)
////                .build();
////        return savingsGoalRepository.save(goal);
////    }
////    NEW CREATE
//public SavingsGoal create(User user, SavingsGoalDTO dto) {
//    LocalDate startDate = (dto.getStartDate() == null || dto.getStartDate().isBlank())
//            ? LocalDate.now()
//            : LocalDate.parse(dto.getStartDate());
//    LocalDate targetDate = LocalDate.parse(dto.getTargetDate());
//
//    // Add validations
//    if (dto.getGoalName() == null || dto.getGoalName().isBlank()) {
//        throw new BadRequestException("Goal name is required");
//    }
//    if (dto.getTargetAmount() == null || dto.getTargetAmount().compareTo(BigDecimal.ZERO) <= 0) {
//        throw new BadRequestException("Target amount must be positive");
//    }
//    if (targetDate.isBefore(LocalDate.now())) {
//        throw new BadRequestException("Target date must be in the future");
//    }
//    if (startDate.isAfter(targetDate)) {
//        throw new BadRequestException("Start date must be before target date");
//    }
//
//    SavingsGoal goal = SavingsGoal.builder()
//            .goalName(dto.getGoalName())
//            .targetAmount(dto.getTargetAmount())
//            .startDate(startDate)
//            .targetDate(targetDate)
//            .user(user)
//            .build();
//
//    return savingsGoalRepository.save(goal);
//}
//
//
//    public List<SavingsGoal> getAll(User user) {
//        return savingsGoalRepository.findByUser(user);
//    }
//
//    public SavingsGoal getById(User user, Long id) {
//        return savingsGoalRepository.findByIdAndUser(id, user)
//                .orElseThrow(() -> new RuntimeException("Goal not found"));
//    }
//
//    public SavingsGoal update(User user, Long id, BigDecimal targetAmount, String targetDate) {
//        SavingsGoal goal = getById(user, id);
//        if (targetAmount != null)
//            goal.setTargetAmount(targetAmount);
//        if (targetDate != null)
//            goal.setTargetDate(LocalDate.parse(targetDate));
//        return savingsGoalRepository.save(goal);
//    }
//
//    public void delete(User user, Long id) {
//        SavingsGoal goal = getById(user, id);
//        savingsGoalRepository.delete(goal);
//    }
//}
// 2nd one updated
package com.sujit.personal_Finance_Manager.service;

import com.sujit.personal_Finance_Manager.dto.SavingsGoalDTO;
import com.sujit.personal_Finance_Manager.entity.CategoryType;
import com.sujit.personal_Finance_Manager.entity.SavingsGoal;
import com.sujit.personal_Finance_Manager.entity.Transaction;
import com.sujit.personal_Finance_Manager.entity.User;
import com.sujit.personal_Finance_Manager.exception.BadRequestException;
import com.sujit.personal_Finance_Manager.repository.SavingsGoalRepository;
import com.sujit.personal_Finance_Manager.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SavingsGoalService {
    private final SavingsGoalRepository savingsGoalRepository;
    private final TransactionRepository transactionRepository;

    public SavingsGoal create(User user, SavingsGoalDTO dto) {
        // Set default start date if null or blank
        LocalDate startDate = (dto.getStartDate() == null || dto.getStartDate().isBlank())
                ? LocalDate.now()
                : LocalDate.parse(dto.getStartDate());

        LocalDate targetDate = LocalDate.parse(dto.getTargetDate());

        // Validation
        if (dto.getGoalName() == null || dto.getGoalName().isBlank()) {
            throw new BadRequestException("Goal name is required");
        }

        if (dto.getTargetAmount() == null || dto.getTargetAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Target amount must be positive");
        }

        if (targetDate.isBefore(LocalDate.now())) {
            throw new BadRequestException("Target date must be in the future");
        }

        if (startDate.isAfter(targetDate)) {
            throw new BadRequestException("Start date must be before target date");
        }

        // Save goal
        SavingsGoal goal = SavingsGoal.builder()
                .goalName(dto.getGoalName())
                .targetAmount(dto.getTargetAmount())
                .startDate(startDate)
                .targetDate(targetDate)
                .user(user)

                .build();

        return savingsGoalRepository.save(goal);  // Save to DB first


//   return savingsGoalRepository.save(goal);
    }



    public List<SavingsGoal> getAll(User user) {
        return savingsGoalRepository.findByUser(user);
    }

    public SavingsGoal getById(User user, Long id) {
        return savingsGoalRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new BadRequestException("Goal not found"));
    }


    public SavingsGoal update(User user, Long id, BigDecimal targetAmount, String targetDate) {
        SavingsGoal goal = getById(user, id);

        if (targetAmount != null) {
            if (targetAmount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BadRequestException("Target amount must be positive");
            }
            goal.setTargetAmount(targetAmount);
        }

        if (targetDate != null) {
            LocalDate newTargetDate = LocalDate.parse(targetDate);
            if (newTargetDate.isBefore(goal.getStartDate())) {
                throw new BadRequestException("Target date cannot be before start date");
            }
            goal.setTargetDate(newTargetDate);
        }

        return savingsGoalRepository.save(goal);
    }

    public void delete(User user, Long id) {
        SavingsGoal goal = getById(user, id);
        savingsGoalRepository.delete(goal);
    }
   // EXTRA

//    public BigDecimal calculateCurrentProgress(User user, SavingsGoal goal) {
//        return transactionRepository.findByUserAndDateBetween(user, goal.getStartDate(), goal.getTargetDate())
//                .stream()
//                .filter(tx -> tx.getCategory() != null && tx.getCategory().getType() == com.sujit.personal_Finance_Manager.entity.CategoryType.SAVINGS)
//                .map(tx -> tx.getAmount() != null ? tx.getAmount() : BigDecimal.ZERO)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//    }


    public BigDecimal calculateCurrentProgress(User user, SavingsGoal goal) {
        System.out.println("▶️ Calculating progress for goal: " + goal.getGoalName());
        System.out.println("Start Date: " + goal.getStartDate());
        System.out.println("End Date: " + goal.getTargetDate());

        List<Transaction> transactions = transactionRepository.findByUserAndDateBetween(
                user,
                goal.getStartDate(),
                goal.getTargetDate()
        );

        System.out.println("Total transactions in range: " + transactions.size());

        for (Transaction tx : transactions) {
            String catName = (tx.getCategory() != null) ? tx.getCategory().getName() : "null";
            String catType = (tx.getCategory() != null && tx.getCategory().getType() != null) ? tx.getCategory().getType().name() : "null";

            System.out.println("Tx Amount: " + tx.getAmount()
                    + " | Category: " + catName
                    + " | Type: " + catType
                    + " | Date: " + tx.getDate());
        }

        BigDecimal income = transactions.stream()
                .filter(tx -> tx.getCategory() != null && tx.getCategory().getType() == CategoryType.INCOME)
                .map(tx -> tx.getAmount() != null ? tx.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal expense = transactions.stream()
                .filter(tx -> tx.getCategory() != null && tx.getCategory().getType() == CategoryType.EXPENSE)
                .map(tx -> tx.getAmount() != null ? tx.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        System.out.println("Total INCOME: " + income);
        System.out.println("Total EXPENSE: " + expense);
        System.out.println("➡️ Progress = Income - Expense = " + income.subtract(expense));

        return income.subtract(expense);
    }





}

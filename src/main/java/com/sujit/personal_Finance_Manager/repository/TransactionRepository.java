package com.sujit.personal_Finance_Manager.repository;

import com.sujit.personal_Finance_Manager.entity.Category;
import com.sujit.personal_Finance_Manager.entity.Transaction;
import com.sujit.personal_Finance_Manager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserOrderByDateDesc(User user);
    List<Transaction> findByUserAndDateBetween(User user, LocalDate start, LocalDate end);
    List<Transaction> findByUserAndCategory(User user, Category category);
    List<Transaction> findByUserAndCategory_Name(User user, String categoryName);
    List<Transaction> findByUserAndDateBetweenAndCategory(User user, LocalDate start, LocalDate end, Category category);
//   / Optional<BigDecimal> sumByUserAndTypeAndDateAfter(User user, String type, LocalDate date);
//
List<Transaction> findByUser(User user);

}




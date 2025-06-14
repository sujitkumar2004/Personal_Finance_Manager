package com.sujit.personal_Finance_Manager.repository;

import com.sujit.personal_Finance_Manager.entity.SavingsGoal;
import com.sujit.personal_Finance_Manager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, Long> {
    List<SavingsGoal> findByUser(User user);
    Optional<SavingsGoal> findByIdAndUser(Long id, User user);



}

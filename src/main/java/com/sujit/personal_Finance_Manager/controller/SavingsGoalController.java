package com.sujit.personal_Finance_Manager.controller;

import com.sujit.personal_Finance_Manager.dto.SavingsGoalDTO;
import com.sujit.personal_Finance_Manager.entity.SavingsGoal;
import com.sujit.personal_Finance_Manager.entity.User;
import com.sujit.personal_Finance_Manager.service.SavingsGoalService;
import com.sujit.personal_Finance_Manager.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class SavingsGoalController {
    private final SavingsGoalService savingsGoalService;
    private final UserService userService;


    //changes
@PostMapping
@ResponseStatus(HttpStatus.CREATED)
public SavingsGoal create(Principal principal,@Valid @RequestBody SavingsGoalDTO dto) {
    User user = userService.findByUsername(principal.getName()).orElseThrow();
    SavingsGoal goal = savingsGoalService.create(user, dto);

    // Enrich transient values after saving
    BigDecimal progress = savingsGoalService.calculateCurrentProgress(user, goal);
    goal.setCurrentProgress(progress);

    BigDecimal percentage = progress.multiply(BigDecimal.valueOf(100))
            .divide(goal.getTargetAmount(), 2, RoundingMode.HALF_UP)
            .stripTrailingZeros();

    goal.setProgressPercentage(
            goal.getTargetAmount().compareTo(BigDecimal.ZERO) > 0
                    ? new BigDecimal(percentage.toPlainString())
                    : BigDecimal.ZERO
    );


    goal.setRemainingAmount(
            goal.getTargetAmount().subtract(progress)
    );

    return goal;
}






    @GetMapping
public List<SavingsGoal> getAll(Principal principal) {
    User user = userService.findByUsername(principal.getName()).orElseThrow();
    List<SavingsGoal> goals = savingsGoalService.getAll(user);

    for (SavingsGoal goal : goals) {
        BigDecimal progress = savingsGoalService.calculateCurrentProgress(user, goal);
        goal.setCurrentProgress(progress);

        if (goal.getTargetAmount().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal percentage = progress.multiply(BigDecimal.valueOf(100))
                    .divide(goal.getTargetAmount(), 2, RoundingMode.HALF_UP)
                    .stripTrailingZeros();

            goal.setProgressPercentage(new BigDecimal(percentage.toPlainString()));
        } else {
            goal.setProgressPercentage(BigDecimal.ZERO);
        }

        goal.setRemainingAmount(goal.getTargetAmount().subtract(progress));
    }

    return goals;
}

@GetMapping("/{id}")
public SavingsGoal getById(Principal principal, @PathVariable Long id) {
    User user = userService.findByUsername(principal.getName()).orElseThrow();
    SavingsGoal goal = savingsGoalService.getById(user, id);

   // goal.setCurrentProgress(savingsGoalService.calculateCurrentProgress(user, goal));
    BigDecimal progress = savingsGoalService.calculateCurrentProgress(user, goal);
    goal.setCurrentProgress(progress);

    if (goal.getTargetAmount().compareTo(BigDecimal.ZERO) > 0) {
        BigDecimal percentage = progress.multiply(BigDecimal.valueOf(100))
                .divide(goal.getTargetAmount(), 2, RoundingMode.HALF_UP)
                .stripTrailingZeros();
        goal.setProgressPercentage(new BigDecimal(percentage.toPlainString()));
    } else {
        goal.setProgressPercentage(BigDecimal.ZERO);
    }

    goal.setRemainingAmount(goal.getTargetAmount().subtract(progress));

    return goal;
}


//    @PutMapping("/{id}")
//    public SavingsGoal update(Principal principal, @PathVariable Long id, @RequestBody SavingsGoalDTO dto) {
//        User user = userService.findByUsername(principal.getName()).orElseThrow();
//        return savingsGoalService.update(user, id, dto.getTargetAmount(), dto.getTargetDate());
//    }
@PutMapping("/{id}")
public SavingsGoal update(@PathVariable Long id, @RequestBody SavingsGoalDTO dto, Principal principal) {
    User user = userService.findByUsername(principal.getName()).orElseThrow();
    SavingsGoal goal = savingsGoalService.update(user, id, dto.getTargetAmount(), dto.getTargetDate()); // ✅


    // Enrich response with calculated fields
    BigDecimal progress = savingsGoalService.calculateCurrentProgress(user, goal);
    goal.setCurrentProgress(progress);

    if (goal.getTargetAmount().compareTo(BigDecimal.ZERO) > 0) {
        BigDecimal percentage = progress.multiply(BigDecimal.valueOf(100))
                .divide(goal.getTargetAmount(), 2, RoundingMode.HALF_UP)
                .stripTrailingZeros();
        goal.setProgressPercentage(new BigDecimal(percentage.toPlainString()));
    } else {
        goal.setProgressPercentage(BigDecimal.ZERO);
    }

    goal.setRemainingAmount(goal.getTargetAmount().subtract(progress));

    return goal;
}


    @DeleteMapping("/{id}")
    public void delete(Principal principal, @PathVariable Long id) {
        User user = userService.findByUsername(principal.getName()).orElseThrow();
        savingsGoalService.delete(user, id);
    }
}

package com.sujit.personal_Finance_Manager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SavingsGoalDTO {
    @NotBlank(message = "Goal name is required")
    private String goalName;
    @NotNull(message = "Target amount is required")
    private BigDecimal targetAmount;
    private String startDate;
    private String targetDate;
}

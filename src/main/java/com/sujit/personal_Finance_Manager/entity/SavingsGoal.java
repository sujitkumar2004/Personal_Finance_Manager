package com.sujit.personal_Finance_Manager.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "savings_goals")
public class SavingsGoal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String goalName;

    @Column(nullable = false)
    private BigDecimal targetAmount;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate targetDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

  @Transient
 private BigDecimal currentProgress;
//
//    @Transient
//    private BigDecimal progressPercentage;
//
@Transient
@JsonProperty("progressPercentage")
public String getProgressPercentageAsString() {
    return progressPercentage != null ? progressPercentage.toPlainString() : "0.0";
}

    @Transient
    @JsonIgnore
    private BigDecimal progressPercentage;






    @Transient
    private BigDecimal remainingAmount;



}

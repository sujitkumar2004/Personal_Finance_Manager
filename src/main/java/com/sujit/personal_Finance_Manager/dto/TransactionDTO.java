package com.sujit.personal_Finance_Manager.dto;

import lombok.Data;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
@Builder
public class TransactionDTO {
    private Long id;
    private BigDecimal amount;
    private String date; // YYYY-MM-DD
    private String category;
    private String description;

    private String type;
}

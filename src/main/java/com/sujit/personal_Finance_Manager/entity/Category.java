package com.sujit.personal_Finance_Manager.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "categories", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "user_id"}))
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // e.g., Salary, Food

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CategoryType type; // INCOME or EXPENSE

    @Column(nullable = false)
    private Boolean isCustom = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}

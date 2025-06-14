package com.sujit.personal_Finance_Manager.service;

import com.sujit.personal_Finance_Manager.dto.ReportDTO;
import com.sujit.personal_Finance_Manager.entity.*;
import com.sujit.personal_Finance_Manager.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ReportServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private ReportService reportService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder().id(1L).username("test@example.com").build();
    }

    @Test
    void getMonthlyReport_ShouldReturnReport_WithIncomeAndExpense() {
        Category salary = Category.builder().id(1L).name("Salary").type(CategoryType.INCOME).user(user).build();
        Category food = Category.builder().id(2L).name("Food").type(CategoryType.EXPENSE).user(user).build();

        Transaction t1 = Transaction.builder()
                .id(10L).amount(5000.0).date(LocalDate.of(2025, 5, 2))
                .category(salary).user(user).build();

        Transaction t2 = Transaction.builder()
                .id(11L).amount(800.0).date(LocalDate.of(2025, 5, 8))
                .category(food).user(user).build();

        when(transactionRepository.findByUserAndDateBetween(
                eq(user), eq(LocalDate.of(2025, 5, 1)), eq(LocalDate.of(2025, 5, 31))
        )).thenReturn(List.of(t1, t2));

        ReportDTO report = reportService.getMonthlyReport(user, 2025, 5);

        assertThat(report.getYear()).isEqualTo(2025);
        assertThat(report.getMonth()).isEqualTo(5);
        assertThat(report.getTotalIncome()).containsEntry("Salary", 5000.0);
        assertThat(report.getTotalExpenses()).containsEntry("Food", 800.0);
        assertThat(report.getNetSavings()).isEqualTo(4200.0);
    }

    @Test
    void getMonthlyReport_ShouldReturnReport_WithNoTransactions() {
        when(transactionRepository.findByUserAndDateBetween(
                eq(user), eq(LocalDate.of(2025, 7, 1)), eq(LocalDate.of(2025, 7, 31))
        )).thenReturn(List.of());

        ReportDTO report = reportService.getMonthlyReport(user, 2025, 7);

        assertThat(report.getYear()).isEqualTo(2025);
        assertThat(report.getMonth()).isEqualTo(7);
        assertThat(report.getTotalIncome()).isEmpty();
        assertThat(report.getTotalExpenses()).isEmpty();
        assertThat(report.getNetSavings()).isZero();
    }

    @Test
    void getYearlyReport_ShouldSumAllMonths() {
        // Spy so we can mock getMonthlyReport without interfering with other logic
        ReportService realService = new ReportService(transactionRepository);
        ReportService spyService = Mockito.spy(realService);

        // 3 months with data, rest empty
        for (int m = 1; m <= 12; m++) {
            ReportDTO.ReportDTOBuilder builder = ReportDTO.builder().year(2025).month(m);
            if (m == 1) {
                builder.totalIncome(Map.of("Salary", 1000.0)).totalExpenses(Map.of("Food", 200.0)).netSavings(800.0);
            } else if (m == 2) {
                builder.totalIncome(Map.of("Salary", 1000.0)).totalExpenses(Map.of("Travel", 100.0)).netSavings(900.0);
            } else if (m == 3) {
                builder.totalIncome(Map.of("Bonus", 500.0)).totalExpenses(Map.of("Gift", 50.0)).netSavings(450.0);
            } else {
                builder.totalIncome(new HashMap<>()).totalExpenses(new HashMap<>()).netSavings(0.0);
            }
            doReturn(builder.build()).when(spyService).getMonthlyReport(user, 2025, m);
        }

        ReportDTO report = spyService.getYearlyReport(user, 2025);

        assertThat(report.getYear()).isEqualTo(2025);
        assertThat(report.getMonth()).isEqualTo(0);
        assertThat(report.getTotalIncome())
                .containsEntry("Salary", 2000.0)
                .containsEntry("Bonus", 500.0);
        assertThat(report.getTotalExpenses())
                .containsEntry("Food", 200.0)
                .containsEntry("Travel", 100.0)
                .containsEntry("Gift", 50.0);
        assertThat(report.getNetSavings()).isEqualTo(800.0 + 900.0 + 450.0); // 2150.0
    }
}

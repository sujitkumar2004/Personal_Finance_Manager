//package com.sujit.personal_Finance_Manager.service;
//
//import com.sujit.personal_Finance_Manager.dto.SavingsGoalDTO;
//import com.sujit.personal_Finance_Manager.entity.SavingsGoal;
//import com.sujit.personal_Finance_Manager.entity.User;
//import com.sujit.personal_Finance_Manager.repository.SavingsGoalRepository;
//import com.sujit.personal_Finance_Manager.repository.TransactionRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//
//import java.time.LocalDate;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.*;
//
//class SavingsGoalServiceTest {
//
//    @Mock
//    private SavingsGoalRepository savingsGoalRepository;
//    @Mock
//    private TransactionRepository transactionRepository;
//
//    @InjectMocks
//    private SavingsGoalService savingsGoalService;
//
//    private User user;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        user = User.builder().id(1L).username("test@example.com").build();
//    }
//
//    @Test
//    void testCreateSavingsGoal() {
//        SavingsGoalDTO dto = new SavingsGoalDTO();
//        dto.setGoalName("Vacation");
//        dto.setTargetAmount(5000.0);
//        dto.setStartDate("2025-07-01");
//        dto.setTargetDate("2025-12-01");
//
//        SavingsGoal goal = SavingsGoal.builder()
//                .id(1L)
//                .goalName("Vacation")
//                .targetAmount(5000.0)
//                .startDate(LocalDate.parse("2025-07-01"))
//                .targetDate(LocalDate.parse("2025-12-01"))
//                .user(user)
//                .build();
//
//        when(savingsGoalRepository.save(any(SavingsGoal.class))).thenReturn(goal);
//
//        SavingsGoal created = savingsGoalService.create(user, dto);
//        assertThat(created.getGoalName()).isEqualTo("Vacation");
//        assertThat(created.getTargetAmount()).isEqualTo(5000.0);
//    }
//}

package com.sujit.personal_Finance_Manager.service;

import com.sujit.personal_Finance_Manager.dto.SavingsGoalDTO;
import com.sujit.personal_Finance_Manager.entity.SavingsGoal;
import com.sujit.personal_Finance_Manager.entity.User;
import com.sujit.personal_Finance_Manager.repository.SavingsGoalRepository;
import com.sujit.personal_Finance_Manager.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class SavingsGoalServiceTest {

    @Mock
    private SavingsGoalRepository savingsGoalRepository;
    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private SavingsGoalService savingsGoalService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder().id(1L).username("alice").build();
    }

    @Test
    void create_ShouldSaveGoal() {
        SavingsGoalDTO dto = SavingsGoalDTO.builder()
                .goalName("Vacation")
                .targetAmount(1000.0)
                .startDate("2025-06-01")
                .targetDate("2025-12-31")
                .build();

        SavingsGoal toSave = SavingsGoal.builder()
                .goalName("Vacation")
                .targetAmount(1000.0)
                .startDate(LocalDate.of(2025, 6, 1))
                .targetDate(LocalDate.of(2025, 12, 31))
                .user(user)
                .build();

        SavingsGoal saved = SavingsGoal.builder()
                .id(2L)
                .goalName("Vacation")
                .targetAmount(1000.0)
                .startDate(LocalDate.of(2025, 6, 1))
                .targetDate(LocalDate.of(2025, 12, 31))
                .user(user)
                .build();

        when(savingsGoalRepository.save(any(SavingsGoal.class))).thenReturn(saved);

        SavingsGoal result = savingsGoalService.create(user, dto);

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getGoalName()).isEqualTo("Vacation");
        assertThat(result.getTargetAmount()).isEqualTo(1000.0);
        assertThat(result.getStartDate()).isEqualTo(LocalDate.of(2025, 6, 1));
    }

    @Test
    void getAll_ShouldReturnGoals() {
        List<SavingsGoal> goals = List.of(
                SavingsGoal.builder().id(1L).user(user).goalName("Trip").build()
        );
        when(savingsGoalRepository.findByUser(user)).thenReturn(goals);

        List<SavingsGoal> result = savingsGoalService.getAll(user);

        assertThat(result).containsExactlyElementsOf(goals);
    }

    @Test
    void getById_ShouldReturnGoal_WhenExists() {
        SavingsGoal goal = SavingsGoal.builder().id(10L).user(user).build();
        when(savingsGoalRepository.findByIdAndUser(10L, user)).thenReturn(Optional.of(goal));

        SavingsGoal result = savingsGoalService.getById(user, 10L);

        assertThat(result).isEqualTo(goal);
    }

    @Test
    void getById_ShouldThrow_WhenNotExists() {
        when(savingsGoalRepository.findByIdAndUser(22L, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> savingsGoalService.getById(user, 22L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Goal not found");
    }

    @Test
    void update_ShouldUpdateFields() {
        SavingsGoal goal = SavingsGoal.builder()
                .id(30L)
                .user(user)
                .targetAmount(500.0)
                .targetDate(LocalDate.of(2025, 12, 31))
                .build();

        when(savingsGoalRepository.findByIdAndUser(30L, user)).thenReturn(Optional.of(goal));
        when(savingsGoalRepository.save(any(SavingsGoal.class))).thenAnswer(i -> i.getArgument(0));

        SavingsGoal result = savingsGoalService.update(user, 30L, 999.0, "2026-01-01");

        assertThat(result.getTargetAmount()).isEqualTo(999.0);
        assertThat(result.getTargetDate()).isEqualTo(LocalDate.of(2026, 1, 1));
    }

    @Test
    void delete_ShouldDeleteGoal() {
        SavingsGoal goal = SavingsGoal.builder().id(44L).user(user).build();
        when(savingsGoalRepository.findByIdAndUser(44L, user)).thenReturn(Optional.of(goal));

        savingsGoalService.delete(user, 44L);

        verify(savingsGoalRepository).delete(goal);
    }
}

//package com.sujit.personal_Finance_Manager.service;
//
//import com.sujit.personal_Finance_Manager.dto.TransactionDTO;
//import com.sujit.personal_Finance_Manager.entity.*;
//import com.sujit.personal_Finance_Manager.repository.TransactionRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//
//import java.time.LocalDate;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.*;
//
//class TransactionServiceTest {
//
//    @Mock
//    private TransactionRepository transactionRepository;
//    @Mock
//    private CategoryService categoryService;
//
//    @InjectMocks
//    private TransactionService transactionService;
//
//    private User user;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        user = User.builder()
//                .id(1L)
//                .username("test@example.com")
//                .build();
//    }
//
//    @Test
//    void testCreateTransaction() {
//        TransactionDTO dto = new TransactionDTO();
//        dto.setAmount(100.0);
//        dto.setDate(LocalDate.now().toString());
//        dto.setCategory("Salary");
//        dto.setDescription("Test salary");
//
//        Category category = Category.builder().id(1L).name("Salary").type(CategoryType.INCOME).build();
//        when(categoryService.findValidCategory(user, "Salary")).thenReturn(category);
//
//        Transaction expectedTx = Transaction.builder()
//                .amount(100.0)
//                .date(LocalDate.now())
//                .category(category)
//                .description("Test salary")
//                .user(user)
//                .build();
//
//        when(transactionRepository.save(any(Transaction.class))).thenReturn(expectedTx);
//
//        Transaction created = transactionService.create(user, dto);
//
//        assertThat(created.getAmount()).isEqualTo(100.0);
//        assertThat(created.getCategory().getName()).isEqualTo("Salary");
//        verify(transactionRepository).save(any(Transaction.class));
//    }
//}
package com.sujit.personal_Finance_Manager.service;

import com.sujit.personal_Finance_Manager.dto.TransactionDTO;
import com.sujit.personal_Finance_Manager.entity.*;
import com.sujit.personal_Finance_Manager.exception.BadRequestException;
import com.sujit.personal_Finance_Manager.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private TransactionService transactionService;

    private User user;
    private Category category;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder().id(1L).username("testuser").build();
        category = Category.builder().id(10L).name("Food").type(CategoryType.EXPENSE).user(user).build();
    }

    // --- Create ---

    @Test
    void create_ShouldSaveTransaction_WhenValid() {
        TransactionDTO dto = TransactionDTO.builder()
                .amount(100.0)
                .date(LocalDate.now().toString())
                .category("Food")
                .description("Lunch")
                .build();

        when(categoryService.findValidCategory(user, "Food")).thenReturn(category);

        Transaction txToSave = Transaction.builder()
                .amount(100.0)
                .date(LocalDate.now())
                .category(category)
                .description("Lunch")
                .user(user)
                .build();

        Transaction saved = Transaction.builder()
                .id(123L)
                .amount(100.0)
                .date(LocalDate.now())
                .category(category)
                .description("Lunch")
                .user(user)
                .build();

        when(transactionRepository.save(any(Transaction.class))).thenReturn(saved);

        Transaction result = transactionService.create(user, dto);

        assertThat(result.getId()).isEqualTo(123L);
        assertThat(result.getAmount()).isEqualTo(100.0);
        assertThat(result.getCategory()).isEqualTo(category);
    }

    @Test
    void create_ShouldThrow_WhenAmountNullOrNegative() {
        TransactionDTO dto = TransactionDTO.builder()
                .amount(-50.0)
                .date(LocalDate.now().toString())
                .category("Food")
                .build();

        assertThatThrownBy(() -> transactionService.create(user, dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Amount must be positive");
    }

    @Test
    void create_ShouldThrow_WhenDateIsFuture() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        TransactionDTO dto = TransactionDTO.builder()
                .amount(10.0)
                .date(tomorrow.toString())
                .category("Food")
                .build();

        assertThatThrownBy(() -> transactionService.create(user, dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Date cannot be in the future");
    }

    // --- Get All ---

    @Test
    void getAll_ShouldReturnTransactions() {
        Transaction tx = Transaction.builder().id(1L).user(user).build();
        List<Transaction> txs = List.of(tx);

        when(transactionRepository.findByUserOrderByDateDesc(user)).thenReturn(txs);

        List<Transaction> result = transactionService.getAll(user);

        assertThat(result).containsExactly(tx);
    }

    // --- Filter ---

    @Test
    void filter_ShouldReturnByDateRangeAndCategory_WhenCategoryProvided() {
        LocalDate start = LocalDate.now().minusDays(5);
        LocalDate end = LocalDate.now();
        String categoryName = "Food";
        List<Transaction> txs = List.of(Transaction.builder().id(1L).user(user).category(category).build());

        when(categoryService.findValidCategory(user, categoryName)).thenReturn(category);
        when(transactionRepository.findByUserAndDateBetweenAndCategory(user, start, end, category)).thenReturn(txs);

        List<Transaction> result = transactionService.filter(user, start, end, categoryName);

        assertThat(result).containsExactlyElementsOf(txs);
    }

    @Test
    void filter_ShouldReturnByDateRange_WhenNoCategory() {
        LocalDate start = LocalDate.now().minusDays(10);
        LocalDate end = LocalDate.now();
        List<Transaction> txs = List.of(Transaction.builder().id(1L).user(user).build());

        when(transactionRepository.findByUserAndDateBetween(user, start, end)).thenReturn(txs);

        List<Transaction> result = transactionService.filter(user, start, end, null);

        assertThat(result).containsExactlyElementsOf(txs);
    }

    // --- Update ---

    @Test
    void update_ShouldUpdateAmountAndDescription_WhenValid() {
        Long txId = 55L;
        Transaction tx = Transaction.builder()
                .id(txId)
                .user(user)
                .amount(50.0)
                .description("Old")
                .build();

        when(transactionRepository.findById(txId)).thenReturn(Optional.of(tx));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        Transaction result = transactionService.update(user, txId, 99.0, "New Desc");

        assertThat(result.getAmount()).isEqualTo(99.0);
        assertThat(result.getDescription()).isEqualTo("New Desc");
    }

    @Test
    void update_ShouldThrow_WhenNotFound() {
        when(transactionRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.update(user, 33L, 100.0, "desc"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void update_ShouldThrow_WhenUserForbidden() {
        Long txId = 101L;
        User otherUser = User.builder().id(2L).build();
        Transaction tx = Transaction.builder().id(txId).user(otherUser).build();

        when(transactionRepository.findById(txId)).thenReturn(Optional.of(tx));

        assertThatThrownBy(() -> transactionService.update(user, txId, 100.0, "desc"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Forbidden");
    }

    // --- Delete ---

    @Test
    void delete_ShouldDelete_WhenValidUser() {
        Long txId = 222L;
        Transaction tx = Transaction.builder().id(txId).user(user).build();

        when(transactionRepository.findById(txId)).thenReturn(Optional.of(tx));

        transactionService.delete(user, txId);

        verify(transactionRepository).delete(tx);
    }

    @Test
    void delete_ShouldThrow_WhenNotFound() {
        when(transactionRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.delete(user, 99L))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void delete_ShouldThrow_WhenUserForbidden() {
        Long txId = 300L;
        User otherUser = User.builder().id(2L).build();
        Transaction tx = Transaction.builder().id(txId).user(otherUser).build();

        when(transactionRepository.findById(txId)).thenReturn(Optional.of(tx));

        assertThatThrownBy(() -> transactionService.delete(user, txId))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Forbidden");
    }
}

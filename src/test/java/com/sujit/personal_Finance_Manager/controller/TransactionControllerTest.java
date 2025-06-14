package com.sujit.personal_Finance_Manager.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sujit.personal_Finance_Manager.dto.TransactionDTO;
import com.sujit.personal_Finance_Manager.entity.Transaction;
import com.sujit.personal_Finance_Manager.entity.User;
import com.sujit.personal_Finance_Manager.service.TransactionService;
import com.sujit.personal_Finance_Manager.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static java.lang.reflect.Array.get;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private UserService userService;

    // your test methods
    @WithMockUser(username = "testuser") // ✅ Simulates a logged-in user
    @Test
    void testCreateTransaction() throws Exception {
        TransactionDTO dto = TransactionDTO.builder()
                .amount(100.0)
                .date("2025-06-12")
                .category("FOOD")
                .description("Lunch")
                .build();

        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(100.0);
        transaction.setDescription("Lunch");

        when(userService.findByUsername("testuser")).thenReturn(Optional.of(new User()));
        when(transactionService.create(any(User.class), any(TransactionDTO.class))).thenReturn(transaction);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.amount").value(100.0))
                .andExpect(jsonPath("$.description").value("Lunch"));
    }



    @WithMockUser(username = "testuser")
    @Test
    void testGetFilteredTransactions() throws Exception {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        Transaction tx = new Transaction();
        tx.setId(2L);
        tx.setAmount(75.5);
        tx.setDescription("Filtered Expense");

        // Define date values
        String startDate = "2025-06-01";
        String endDate = "2025-06-30";
        String category = "FOOD";

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        // Mock dependencies
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(transactionService.filter(user, start, end, category)).thenReturn(List.of(tx));

        // Act & Assert
        mockMvc.perform(get("/api/transactions")
                        .param("startDate", startDate)
                        .param("endDate", endDate)
                        .param("category", category))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].amount").value(75.5))
                .andExpect(jsonPath("$[0].description").value("Filtered Expense"));
    }

    @WithMockUser(username = "testuser")
    @Test
    void testUpdateTransaction() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        TransactionDTO dto = TransactionDTO.builder()
                .amount(999.99)
                .description("Updated test transaction")
                .build();

        Transaction updated = new Transaction();
        updated.setId(10L);
        updated.setAmount(999.99);
        updated.setDescription("Updated test transaction");

        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(transactionService.update(user, 10L, dto.getAmount(), dto.getDescription()))
                .thenReturn(updated);

        mockMvc.perform(put("/api/transactions/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.amount").value(999.99))
                .andExpect(jsonPath("$.description").value("Updated test transaction"));
    }


    @WithMockUser(username = "testuser")
    @Test
    void testDeleteTransaction() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        Mockito.doNothing().when(transactionService).delete(user, 20L);

        mockMvc.perform(delete("/api/transactions/20"))
                .andExpect(status().isOk()); // or isNoContent() depending on how you configure the response
    }



}

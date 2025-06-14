package com.sujit.personal_Finance_Manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sujit.personal_Finance_Manager.dto.SavingsGoalDTO;
import com.sujit.personal_Finance_Manager.entity.SavingsGoal;
import com.sujit.personal_Finance_Manager.entity.User;
import com.sujit.personal_Finance_Manager.service.SavingsGoalService;
import com.sujit.personal_Finance_Manager.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SavingsGoalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SavingsGoalService savingsGoalService;

    @MockBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private User getMockUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        return user;
    }

    private SavingsGoal getMockGoal() {
        SavingsGoal goal = new SavingsGoal();
        goal.setId(1L);
        goal.setTargetAmount(5000.0);
        goal.setTargetDate(LocalDate.of(2025, 12, 31));
        return goal;
    }

    @WithMockUser(username = "testuser")
    @Test
    void testCreateSavingsGoal() throws Exception {
        User user = getMockUser();
        SavingsGoalDTO dto = new SavingsGoalDTO();
        dto.setTargetAmount(5000.0);
        dto.setTargetDate("2025-12-31");

        SavingsGoal goal = getMockGoal();

        Mockito.when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        Mockito.when(savingsGoalService.create(eq(user), any(SavingsGoalDTO.class))).thenReturn(goal);

        mockMvc.perform(post("/api/goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.targetAmount").value(5000.0));
    }

    @WithMockUser(username = "testuser")
    @Test
    void testGetAllSavingsGoals() throws Exception {
        User user = getMockUser();
        SavingsGoal goal = getMockGoal();

        Mockito.when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        Mockito.when(savingsGoalService.getAll(user)).thenReturn(List.of(goal));

        mockMvc.perform(get("/api/goals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @WithMockUser(username = "testuser")
    @Test
    void testGetSavingsGoalById() throws Exception {
        User user = getMockUser();
        SavingsGoal goal = getMockGoal();

        Mockito.when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        Mockito.when(savingsGoalService.getById(user, 1L)).thenReturn(goal);

        mockMvc.perform(get("/api/goals/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @WithMockUser(username = "testuser")
    @Test
    void testUpdateSavingsGoal() throws Exception {
        User user = getMockUser();
        SavingsGoalDTO dto = new SavingsGoalDTO();
        dto.setTargetAmount(10000.0);
        dto.setTargetDate("2026-01-01");

        SavingsGoal updated = new SavingsGoal();
        updated.setId(1L);
        updated.setTargetAmount(10000.0);
        updated.setTargetDate(LocalDate.parse("2026-01-01"));

        Mockito.when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        Mockito.when(savingsGoalService.update(user, 1L, 10000.0, String.valueOf(LocalDate.parse("2026-01-01"))))
                .thenReturn(updated);

        mockMvc.perform(put("/api/goals/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.targetAmount").value(10000.0));
    }

    @WithMockUser(username = "testuser")
    @Test
    void testDeleteSavingsGoal() throws Exception {
        User user = getMockUser();

        Mockito.when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        Mockito.doNothing().when(savingsGoalService).delete(user, 1L);

        mockMvc.perform(delete("/api/goals/1"))
                .andExpect(status().isOk());
    }
}

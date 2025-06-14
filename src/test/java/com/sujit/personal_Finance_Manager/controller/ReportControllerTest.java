package com.sujit.personal_Finance_Manager.controller;

import com.sujit.personal_Finance_Manager.dto.ReportDTO;
import com.sujit.personal_Finance_Manager.entity.User;
import com.sujit.personal_Finance_Manager.service.ReportService;
import com.sujit.personal_Finance_Manager.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService reportService;

    @MockBean
    private UserService userService;

    private User getMockUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        return user;
    }

    private ReportDTO getMockReport(int year, int month) {
        return ReportDTO.builder()
                .year(year)
                .month(month)
                .totalIncome(Map.of("Salary", 10000.0))
                .totalExpenses(Map.of("Food", 3000.0))
                .netSavings(7000.0)
                .build();
    }

    @WithMockUser(username = "testuser")
    @Test
    void testGetMonthlyReport() throws Exception {
        User user = getMockUser();
        ReportDTO report = getMockReport(2025, 6);

        Mockito.when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        Mockito.when(reportService.getMonthlyReport(user, 2025, 6)).thenReturn(report);

        mockMvc.perform(get("/api/reports/monthly/2025/6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.year").value(2025))
                .andExpect(jsonPath("$.month").value(6))
                .andExpect(jsonPath("$.netSavings").value(7000.0))
                .andExpect(jsonPath("$.totalIncome.Salary").value(10000.0))
                .andExpect(jsonPath("$.totalExpenses.Food").value(3000.0));
    }

    @WithMockUser(username = "testuser")
    @Test
    void testGetYearlyReport() throws Exception {
        User user = getMockUser();
        ReportDTO report = getMockReport(2025, 0); // 0 means not specific to a month

        Mockito.when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        Mockito.when(reportService.getYearlyReport(user, 2025)).thenReturn(report);

        mockMvc.perform(get("/api/reports/yearly/2025"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.year").value(2025))
                .andExpect(jsonPath("$.netSavings").value(7000.0));
    }
}

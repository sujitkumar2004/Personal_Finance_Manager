package com.sujit.personal_Finance_Manager.controller;

import com.sujit.personal_Finance_Manager.dto.ReportDTO;
import com.sujit.personal_Finance_Manager.entity.User;
import com.sujit.personal_Finance_Manager.exception.BadRequestException;
import com.sujit.personal_Finance_Manager.service.ReportService;
import com.sujit.personal_Finance_Manager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;
    private final UserService userService;

    @GetMapping("/monthly/{year}/{month}")
    public ReportDTO getMonthlyReport(
            Principal principal,
            @PathVariable int year,
            @PathVariable int month
    ) {
        if (month < 1 || month > 12) {
            throw new BadRequestException("Invalid month. Must be between 1 and 12.");
        }
        User user = userService.findByUsername(principal.getName()).orElseThrow();
        return reportService.getMonthlyReport(user, year, month);
    }

    @GetMapping("/yearly/{year}")
    public ReportDTO getYearlyReport(
            Principal principal,
            @PathVariable int year
    ) {
        User user = userService.findByUsername(principal.getName()).orElseThrow();
        return reportService.getYearlyReport(user, year);
    }
}

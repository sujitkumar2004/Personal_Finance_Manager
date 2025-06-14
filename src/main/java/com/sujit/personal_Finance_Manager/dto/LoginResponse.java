package com.sujit.personal_Finance_Manager.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private Long userId;
    private String token;
    private String message;
}

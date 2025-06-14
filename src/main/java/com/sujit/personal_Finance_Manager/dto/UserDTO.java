//package com.sujit.personal_Finance_Manager.dto;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//
//@Data
//
//public class UserDTO {
//    private String username;
//    private String password;
//    private String fullName;
//    private String phoneNumber;
//
//
//}

package com.sujit.personal_Finance_Manager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    @NotBlank(message = "Username (email) is required")
    @Email(message = "Username must be a valid email")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
}

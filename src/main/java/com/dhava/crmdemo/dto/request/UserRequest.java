package com.dhava.crmdemo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRequest {

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "email is required")
    @Email(message = "please provide a valid email")
    private String email;

    @NotBlank(message = "phone no. is required")
    private String phone;

    private Boolean isActive;
}
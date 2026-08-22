package com.dhava.crmdemo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LeadRequest {

    @NotBlank(message = "name is required")
    private String leadName;

    @NotBlank(message = "email is required")
    @Email(message = "please provide a valid email")
    private String email;

    @NotBlank(message = "phone no. is required")
    @Pattern(
            regexp = "^\\+?[1-9]\\d{7,14}$",
            message = "Phone number must be a valid international format"
    )
    private String phone;

    private String source;

    private String description;

    private BigDecimal expectedBudget;
}

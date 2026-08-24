package com.dhava.crmdemo.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LeadToProjectRequest {
    @NotBlank(message = "project name is required")
    private String projectName;

    @NotBlank(message = "client name is required")
    private String clientName;

    private String description;

    @NotNull(message = "final budget is required")
    @DecimalMin(value = "0.0", inclusive = false,
            message = "final budget must be greater than zero")
    private BigDecimal finalBudget;

    private LocalDate startDate = LocalDate.now();

    private LocalDate endDate;
}

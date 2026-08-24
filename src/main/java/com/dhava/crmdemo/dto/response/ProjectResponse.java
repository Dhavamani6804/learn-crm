package com.dhava.crmdemo.dto.response;

import com.dhava.crmdemo.enums.ProjectStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ProjectResponse {

    private Long id;
    private String projectName;
    private String clientName;
    private Long leadId;
    private String description;
    private BigDecimal finalBudget;
    private ProjectStatus status;
    private Long assignedUserId;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
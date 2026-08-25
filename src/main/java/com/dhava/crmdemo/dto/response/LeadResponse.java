package com.dhava.crmdemo.dto.response;

import com.dhava.crmdemo.enums.LeadStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class LeadResponse {

    private String id;
    private String leadName;
    private String email;
    private String phone;
    private String source;
    private LeadStatus status;
    private Long assignedUserId;
    private String assignedUserName;
    private String description;
    private BigDecimal expectedBudget;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
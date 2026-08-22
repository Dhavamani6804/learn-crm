package com.dhava.crmdemo.entity;

import com.dhava.crmdemo.enums.LeadStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "leads")
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "name is required")
    private String leadName;

    @NotBlank(message = "email is required")
    @Email(message = "please provide a valid email")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "phone no. is required")
    @Pattern(
            regexp = "^\\+?[1-9]\\d{7,14}$",
            message = "Phone number must be a valid international format"
    )
    @Column(unique = true)
    private String phone;

    private String source;

    @Enumerated(EnumType.STRING)
    private LeadStatus status;

    private Long assignedUserId;

    @Column(columnDefinition = "TEXT")
    private String description;

    private BigDecimal expectedBudget;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Lead(String leadName, String email, String phone, String source, LeadStatus status, Long assignedUserId, String description, BigDecimal expectedBudget, LocalDateTime createdDate, LocalDateTime updatedDate) {
        this.leadName = leadName;
        this.email = email;
        this.phone = phone;
        this.source = source;
        this.status = status;
        this.assignedUserId = assignedUserId;
        this.description = description;
        this.expectedBudget = expectedBudget;
        this.createdAt = createdDate;
        this.updatedAt = updatedDate;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

package com.dhava.crmdemo.entity;

import com.dhava.crmdemo.enums.LeadStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "leads")
public class Lead {

    @Id
    private String id;

    @NotBlank(message = "name is required")
    private String leadName;

    @NotBlank(message = "email is required")
    @Email(message = "please provide a valid email")
    @Indexed(unique = true)
    private String email;

    @NotBlank(message = "phone no. is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{7,14}$", message = "Phone number must be a valid international format")
    @Indexed(unique = true)
    private String phone;

    private String source;

    private LeadStatus status;

    private Long assignedUserId;

    private String description;

    private BigDecimal expectedBudget;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

}
package com.dhava.crmdemo.dto.response;

import com.dhava.crmdemo.enums.ActivityType;
import com.dhava.crmdemo.enums.EntityType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActivityLogResponse {

    private String id;
    private EntityType entityType;
    private String entityId;
    private ActivityType activityType;
    private String message;
    private String performedBy;
    private LocalDateTime timestamp;
    private String oldValue;
    private String newValue;
}
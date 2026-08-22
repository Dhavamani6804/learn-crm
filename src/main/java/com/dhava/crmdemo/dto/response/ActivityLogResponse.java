package com.dhava.crmdemo.dto.response;

import com.dhava.crmdemo.enums.ActivityType;
import com.dhava.crmdemo.enums.EntityType;
import lombok.Data;


import java.time.LocalDateTime;

@Data
public class ActivityLogResponse {

    private Long id;
    private EntityType entityType;
    private Long entityId;
    private ActivityType activityType;
    private String message;
    private Long performedBy;
    private LocalDateTime timestamp;
    private String oldValue;
    private String newValue;
}

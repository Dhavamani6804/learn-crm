package com.dhava.crmdemo.entity;

import com.dhava.crmdemo.enums.ActivityType;
import com.dhava.crmdemo.enums.EntityType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "activity_logs")
public class ActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EntityType entityType;

    private Long entityId;

    @Enumerated(EnumType.STRING)
    private ActivityType activityType;

    @Column(columnDefinition = "TEXT")
    private String message;

    private Long performedBy;

    private LocalDateTime timestamp;

    @Column(columnDefinition = "TEXT")
    private String oldValue;
    
    @Column(columnDefinition = "TEXT")
    private String newValue;

    public ActivityLog(EntityType entityType, Long entityId, ActivityType activityType, String message, Long performedBy, LocalDateTime timeStamp, String oldValue, String newValue) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.activityType = activityType;
        this.message = message;
        this.performedBy = performedBy;
        this.timestamp = timeStamp;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}

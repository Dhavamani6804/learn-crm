package com.dhava.crmdemo.entity;

import com.dhava.crmdemo.enums.ActivityType;
import com.dhava.crmdemo.enums.EntityType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "activity_logs")
@CompoundIndex(
        name = "entity_type_id_idx",
        def = "{'entityType': 1, 'entityId': 1}"
)
public class ActivityLog {

    @Id
    private String id;

    private EntityType entityType;

    private String entityId;

    private ActivityType activityType;

    private String message;

    private String performedBy;

    @CreatedDate
    private LocalDateTime timestamp;

    private String oldValue;

    private String newValue;

}
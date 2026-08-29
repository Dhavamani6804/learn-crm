package com.dhava.crmdemo.mapper;

import com.dhava.crmdemo.dto.response.ActivityLogResponse;
import com.dhava.crmdemo.entity.ActivityLog;
import org.springframework.stereotype.Component;

@Component
public class ActivityLogMapper {

    public ActivityLogResponse toActivityLogResponse(ActivityLog activityLog, String performedBy) {

        ActivityLogResponse response = new ActivityLogResponse();

        response.setId(activityLog.getId());
        response.setEntityType(activityLog.getEntityType());
        response.setEntityId(activityLog.getEntityId());
        response.setActivityType(activityLog.getActivityType());
        response.setMessage(activityLog.getMessage());
        response.setPerformedBy(performedBy);
        response.setTimestamp(activityLog.getTimestamp());
        response.setOldValue(activityLog.getOldValue());
        response.setNewValue(activityLog.getNewValue());

        return response;
    }
}
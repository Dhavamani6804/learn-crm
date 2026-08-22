package com.dhava.crmdemo.mapper;

import com.dhava.crmdemo.dto.response.ActivityLogResponse;
import com.dhava.crmdemo.entity.ActivityLog;
import org.springframework.stereotype.Component;

@Component
public class ActivityLogMapper {

    public ActivityLogResponse toActivityLogResponse(ActivityLog activityLog) {

        ActivityLogResponse activityLogResponse = new ActivityLogResponse();

        activityLogResponse.setId(activityLog.getId());
        activityLogResponse.setEntityType(activityLog.getEntityType());
        activityLogResponse.setEntityId(activityLog.getEntityId());
        activityLogResponse.setActivityType(activityLog.getActivityType());
        activityLogResponse.setMessage(activityLog.getMessage());
        activityLogResponse.setPerformedBy(activityLog.getPerformedBy());
        activityLogResponse.setTimestamp(activityLog.getTimestamp());
        activityLogResponse.setOldValue(activityLog.getOldValue());
        activityLogResponse.setNewValue(activityLog.getNewValue());

        return activityLogResponse;
    }
}

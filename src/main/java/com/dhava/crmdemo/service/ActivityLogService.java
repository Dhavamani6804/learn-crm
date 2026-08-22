package com.dhava.crmdemo.service;

import com.dhava.crmdemo.dto.response.ActivityLogResponse;
import com.dhava.crmdemo.entity.ActivityLog;
import com.dhava.crmdemo.enums.ActivityType;
import com.dhava.crmdemo.enums.EntityType;
import com.dhava.crmdemo.mapper.ActivityLogMapper;
import com.dhava.crmdemo.repository.ActivityLogRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class ActivityLogService {
    private final ActivityLogRepository activityLogRepository;
    private final ActivityLogMapper activityLogMapper;

    public void logActivity(EntityType entityType, Long entityId, ActivityType activityType, String message, Long performedBy, String oldValue, String newValue) {
        ActivityLog activityLog = new ActivityLog();
        activityLog.setEntityType(entityType);
        activityLog.setEntityId(entityId);
        activityLog.setActivityType(activityType);
        activityLog.setMessage(message);
        activityLog.setPerformedBy(performedBy);
        activityLog.setOldValue(oldValue);
        activityLog.setNewValue(newValue);
        activityLogRepository.save(activityLog);
    }

    public List<ActivityLogResponse> getAllLogs() {
        return activityLogRepository.findAll()
                .stream()
                .map(activityLogMapper::toActivityLogResponse)
                .toList();
    }

    public List<ActivityLogResponse> getLogsByEntity(EntityType entityType, Long entityId) {
        return activityLogRepository.findByEntityTypeAndEntityId(entityType,entityId);
    }

}

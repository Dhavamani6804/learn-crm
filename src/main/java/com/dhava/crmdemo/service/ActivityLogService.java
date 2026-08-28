package com.dhava.crmdemo.service;

import com.dhava.crmdemo.dto.response.ActivityLogResponse;
import com.dhava.crmdemo.entity.ActivityLog;
import com.dhava.crmdemo.entity.User;
import com.dhava.crmdemo.enums.ActivityType;
import com.dhava.crmdemo.enums.EntityType;
import com.dhava.crmdemo.enums.Role;
import com.dhava.crmdemo.exception.AuthorizationException;
import com.dhava.crmdemo.mapper.ActivityLogMapper;
import com.dhava.crmdemo.repository.ActivityLogRepository;
import com.dhava.crmdemo.security.SecurityUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final ActivityLogMapper activityLogMapper;
    private final SecurityUtils securityUtils;

    public void logActivity(EntityType entityType, String entityId, ActivityType activityType, String message, String oldValue, String newValue) {

        User actor = securityUtils.getCurrentUser();

        ActivityLog activityLog = new ActivityLog();

        activityLog.setEntityType(entityType);
        activityLog.setEntityId(entityId);
        activityLog.setActivityType(activityType);
        activityLog.setMessage(message);
        activityLog.setPerformedBy(actor.getName());
        activityLog.setOldValue(oldValue);
        activityLog.setNewValue(newValue);

        activityLogRepository.save(activityLog);
    }

    public List<ActivityLogResponse> getAllLogs() {
        User actor = securityUtils.getCurrentUser();
        if (actor.getRole() != Role.SUPER_ADMIN) {
            throw new AuthorizationException("Only SUPER_ADMIN can access all activity logs");
        }
        return activityLogRepository.findAll().stream().map(activityLogMapper::toActivityLogResponse).toList();
    }

    public List<ActivityLogResponse> getLogsByEntity(EntityType entityType, String entityId) {

        User actor = securityUtils.getCurrentUser();
        validiateLogAccess(actor, entityType, entityId);
        return activityLogRepository.findByEntityTypeAndEntityId(entityType, entityId).stream().map(activityLogMapper::toActivityLogResponse).toList();
    }

    private void validiateLogAccess(User actor, EntityType entityType, String entityId) {

        if (actor.getRole() == Role.SUPER_ADMIN) {
            return;
        }

        if (actor.getRole() == Role.ADMIN) {
            if (entityType == EntityType.USER || entityType == EntityType.LEAD || entityType == EntityType.PROJECT) {
                return;
            }
            throw new AuthorizationException("Admin does not have access to these activity logs");
        }

        if (actor.getRole() == Role.USER) {
            if (entityType == EntityType.USER) {
                if (!entityId.equals(String.valueOf(actor.getId()))) {
                    throw new AuthorizationException("User can only access their own activity logs");
                }
                return;
            }
            if (entityType == EntityType.LEAD || entityType == EntityType.PROJECT) {
                return;
            }
            throw new AuthorizationException("User does not have access to this activity logs");
        }
        throw new AuthorizationException("You don't have access to this activity logs");
    }
}
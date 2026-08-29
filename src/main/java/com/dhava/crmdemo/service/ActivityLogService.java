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
import com.dhava.crmdemo.repository.UserRepository;
import com.dhava.crmdemo.security.SecurityUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final ActivityLogMapper activityLogMapper;
    private final SecurityUtils securityUtils;
    private final UserRepository userRepository;

    public void logActivity(EntityType entityType, String entityId, ActivityType activityType, String message, String oldValue, String newValue) {

        User actor = securityUtils.getCurrentUser();

        ActivityLog activityLog = new ActivityLog();

        activityLog.setEntityType(entityType);
        activityLog.setEntityId(entityId);
        activityLog.setActivityType(activityType);
        activityLog.setMessage(message);
        activityLog.setPerformedBy(actor.getId());
        activityLog.setOldValue(oldValue);
        activityLog.setNewValue(newValue);

        activityLogRepository.save(activityLog);
    }

    public List<ActivityLogResponse> getAllLogs() {

        User actor = securityUtils.getCurrentUser();

        if (actor.getRole() != Role.SUPER_ADMIN) {
            throw new AuthorizationException("Only SUPER_ADMIN can access all activity logs");
        }

        List<ActivityLog> logs = activityLogRepository.findAll();
        return mapLogs(logs);
    }

    public List<ActivityLogResponse> getLogsByEntity(EntityType entityType, String entityId) {

        User actor = securityUtils.getCurrentUser();
        validateLogAccess(actor, entityType, entityId);
        List<ActivityLog> logs = activityLogRepository.findByEntityTypeAndEntityId(entityType, entityId);
        return mapLogs(logs);
    }

    private List<ActivityLogResponse> mapLogs(List<ActivityLog> logs) {

        if (logs.isEmpty()) {
            return List.of();
        }

        Set<Long> userIds = logs.stream()
                .map(ActivityLog::getPerformedBy)
                .collect(Collectors.toSet());

        Map<Long, String> userNames = userRepository.findAllById(userIds)
                .stream()
                .collect(Collectors.toMap(User::getId, User::getName));

        return logs.stream().map(log -> activityLogMapper.toActivityLogResponse(log, userNames.get(log.getPerformedBy()))).toList();
    }

    private void validateLogAccess(User actor, EntityType entityType, String entityId) {

        if (actor.getRole() == Role.SUPER_ADMIN) {
            return;
        }

        if (entityType == EntityType.LEAD || entityType == EntityType.PROJECT) {

            if (actor.getRole() == Role.ADMIN || actor.getRole() == Role.USER) {
                return;
            }

            throw new AuthorizationException("You don't have access to these activity logs");
        }

        if (entityType == EntityType.USER) {

            long targetUserId;

            try {
                targetUserId = Long.parseLong(entityId);
            } catch (NumberFormatException e) {
                throw new AuthorizationException("Invalid user id");
            }

            User targetUser = userRepository.findById(targetUserId).orElseThrow(() -> new AuthorizationException("User not found"));

            if (actor.getRole() == Role.ADMIN) {

                if (actor.getId().equals(targetUser.getId())) {
                    return;
                }

                if (targetUser.getRole() == Role.USER) {
                    return;
                }
                throw new AuthorizationException("ADMIN cannot access this user's activity logs");
            }

            if (actor.getRole() == Role.USER) {

                if (actor.getId().equals(targetUser.getId())) {
                    return;
                }
                throw new AuthorizationException("USER can only access their own activity logs");
            }
            throw new AuthorizationException("You don't have access to these activity logs");
        }
        throw new AuthorizationException("You don't have access to these activity logs");
    }
}
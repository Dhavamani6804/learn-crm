package com.dhava.crmdemo.controller;

import com.dhava.crmdemo.api.ApiResponse;
import com.dhava.crmdemo.dto.response.ActivityLogResponse;
import com.dhava.crmdemo.enums.EntityType;
import com.dhava.crmdemo.service.ActivityLogService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/activity-logs")
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ActivityLogResponse>>> getAllLogs() {
        return ResponseEntity.ok(ApiResponse.ok(activityLogService.getAllLogs(), "Fetched all activity logs"));
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping("/{entityType}/{entityId}")
    public ResponseEntity<ApiResponse<List<ActivityLogResponse>>> getLogsByEntity(@PathVariable EntityType entityType, @PathVariable String entityId) {
        return ResponseEntity.ok(ApiResponse.ok(activityLogService.getLogsByEntity(entityType, entityId), "Fetched activity logs"));
    }
}
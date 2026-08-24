package com.dhava.crmdemo.controller;

import com.dhava.crmdemo.api.ApiResponse;
import com.dhava.crmdemo.dto.response.ActivityLogResponse;
import com.dhava.crmdemo.enums.EntityType;
import com.dhava.crmdemo.service.ActivityLogService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/activity-logs")
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ActivityLogResponse>>> getAllLogs() {
        return ResponseEntity.ok(ApiResponse.ok(activityLogService.getAllLogs(), "Fetched all activity logs"));
    }

    @GetMapping("/{entityType}/{entityId}")
    public ResponseEntity<ApiResponse<List<ActivityLogResponse>>> getLogsByEntity(@PathVariable EntityType entityType, @PathVariable String entityId) {
        return ResponseEntity.ok(ApiResponse.ok(activityLogService.getLogsByEntity(entityType, entityId), "Fetched activity logs"));
    }
}
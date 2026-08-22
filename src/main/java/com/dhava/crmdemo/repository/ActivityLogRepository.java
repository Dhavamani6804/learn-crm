package com.dhava.crmdemo.repository;

import com.dhava.crmdemo.dto.response.ActivityLogResponse;
import com.dhava.crmdemo.entity.ActivityLog;
import com.dhava.crmdemo.enums.EntityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLogResponse>findByEntityTypeAndEntityId(EntityType entityType, Long entityId);
}

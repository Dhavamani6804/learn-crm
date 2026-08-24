package com.dhava.crmdemo.repository;

import com.dhava.crmdemo.entity.ActivityLog;
import com.dhava.crmdemo.enums.EntityType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ActivityLogRepository {

    private final MongoTemplate mongoTemplate;

    public void save(ActivityLog activityLog) {
        mongoTemplate.save(activityLog);
    }

    public List<ActivityLog> findByEntityTypeAndEntityId(EntityType entityType, String entityId) {

        Query query = new Query(Criteria.where("entityType").is(entityType).and("entityId").is(entityId));

        query.with(Sort.by(Sort.Direction.ASC, "timestamp"));

        return mongoTemplate.find(query, ActivityLog.class);
    }

    public List<ActivityLog> findAll() {
        return mongoTemplate.findAll(ActivityLog.class);
    }
}
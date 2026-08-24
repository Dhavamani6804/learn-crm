package com.dhava.crmdemo.repository;

import com.dhava.crmdemo.entity.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProjectRepository {

    private final MongoTemplate mongoTemplate;

    public Project save(Project project) {
        return mongoTemplate.save(project);
    }

    public List<Project> findAll() {
        return mongoTemplate.findAll(Project.class);
    }

    public Optional<Project> findById(String id) {
        Project project = mongoTemplate.findById(id, Project.class);
        return Optional.ofNullable(project);
    }

    public boolean existsByLeadId(String leadId) {
        Query query = new Query(Criteria.where("leadId").is(leadId));

        return mongoTemplate.exists(query, Project.class);
    }

    public void deleteById(String id) {
        Query query = new Query(Criteria.where("_id").is(id));

        mongoTemplate.remove(query, Project.class);
    }
}
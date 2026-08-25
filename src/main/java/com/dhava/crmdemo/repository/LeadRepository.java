package com.dhava.crmdemo.repository;

import com.dhava.crmdemo.entity.Lead;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for accessing Lead data.
 *
 * <p>Provides database operations for Lead.
 *
 * @author Dhava
 * @since 1.0
 */

@Repository
@RequiredArgsConstructor
public class LeadRepository {

    private final MongoTemplate mongoTemplate;

    public Lead save(Lead lead) {
        return mongoTemplate.save(lead);
    }

    public List<Lead> findAll() {
        return mongoTemplate.findAll(Lead.class);
    }

    public Optional<Lead> findById(String id) {
        Lead lead = mongoTemplate.findById(id, Lead.class);
        return Optional.ofNullable(lead);
    }

    public boolean existsByEmail(String email) {
        Query query = new Query(Criteria.where("email").is(email));

        return mongoTemplate.exists(query, Lead.class);
    }

    public boolean existsByPhone(String phone) {
        Query query = new Query(Criteria.where("phone").is(phone));

        return mongoTemplate.exists(query, Lead.class);
    }

    public boolean existsByEmailAndIdNot(String email, String id) {
        Query query = new Query(Criteria.where("email").is(email).and("_id").ne(id));

        return mongoTemplate.exists(query, Lead.class);
    }

    public boolean existsByPhoneAndIdNot(String phone, String id) {
        Query query = new Query(Criteria.where("phone").is(phone).and("_id").ne(id));

        return mongoTemplate.exists(query, Lead.class);
    }

    public void deleteById(String id) {
        Query query = new Query(Criteria.where("_id").is(id));

        mongoTemplate.remove(query, Lead.class);
    }
}
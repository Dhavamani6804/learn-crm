package com.dhava.crmdemo.repository;

import com.dhava.crmdemo.entity.Lead;
import com.dhava.crmdemo.enums.LeadSortBy;
import com.dhava.crmdemo.enums.LeadStatus;
import com.dhava.crmdemo.enums.SortDirection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.bson.Document;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
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

    public LeadPageResult findAll(int pageNo, int pageSize, LeadSortBy sortBy, SortDirection sortDirection, String source, LeadStatus status, BigDecimal minBudget, BigDecimal maxBudget) {


        Criteria criteria = new Criteria();

        // Filters
        List<Criteria> filters = new ArrayList<>();

        if (source != null && !source.isBlank()) {
            filters.add(Criteria.where("source").is(source));
        }

        if (status != null) {
            filters.add(Criteria.where("status").is(status));
        }

        if (minBudget != null || maxBudget != null) {

            Criteria budgetCriteria = Criteria.where("expectedBudget");

            if (minBudget != null) {
                budgetCriteria.gte(minBudget);
            }

            if (maxBudget != null) {
                budgetCriteria.lte(maxBudget);
            }

            filters.add(budgetCriteria);
        }

        if (!filters.isEmpty()) {
            criteria.andOperator(filters.toArray(new Criteria[0]));
        }

        // Total elements
        Query countQuery = new Query(criteria);
        long totalElements = mongoTemplate.count(countQuery, Lead.class);

        long offset = (long) pageNo * pageSize;

        List<Lead> leads;

        if (sortBy == LeadSortBy.ASSIGNED_USER_NAME) {

            leads = findAllSortedByAssignedUserName(criteria, offset, pageSize, sortDirection);

        } else {

            Query query = new Query(criteria);

            String sortField = switch (sortBy) {
                case CREATED_AT -> "createdAt";
                case UPDATED_AT -> "updatedAt";
                default -> throw new IllegalStateException("Unexpected value: " + sortBy);
            };

            Sort.Direction direction = sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;

            query.with(Sort.by(new Sort.Order(direction, sortField), Sort.Order.asc("_id")));

            query.skip(offset);
            query.limit(pageSize);

            leads = mongoTemplate.find(query, Lead.class);
        }

        return LeadPageResult.builder().leads(leads).totalElements(totalElements).build();
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

    private List<Lead> findAllSortedByAssignedUserName(Criteria criteria, long offset, int pageSize, SortDirection sortDirection) {

        Sort.Direction direction = sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;

        Document assignedUserNameNullExpression = new Document("$cond", Arrays.asList(new Document("$eq", Arrays.asList(new Document("$ifNull", Arrays.asList("$assignedUserName", "")), "")), 1, 0));

        Aggregation aggregation = Aggregation.newAggregation(

                Aggregation.match(criteria),

                Aggregation.addFields().addFieldWithValue("assignedUserNameNull", assignedUserNameNullExpression).build(),

                Aggregation.sort(Sort.by(Sort.Order.asc("assignedUserNameNull"), new Sort.Order(direction, "assignedUserName"), Sort.Order.desc("createdAt"), Sort.Order.asc("_id"))),

                Aggregation.skip(offset),

                Aggregation.limit(pageSize));

        AggregationResults<Lead> results = mongoTemplate.aggregate(aggregation, "leads", Lead.class);

        return results.getMappedResults();
    }
}
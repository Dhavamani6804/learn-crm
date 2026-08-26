package com.dhava.crmdemo.repository;

import com.dhava.crmdemo.dto.cursor.ProjectCursor;
import com.dhava.crmdemo.dto.request.ProjectFilterRequest;
import com.dhava.crmdemo.entity.Project;
import com.dhava.crmdemo.enums.ProjectSortBy;
import com.dhava.crmdemo.enums.SortDirection;
import com.dhava.crmdemo.util.ProjectCursorUtil;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProjectRepository {

    private final MongoTemplate mongoTemplate;
    private final ProjectCursorUtil projectCursorUtil;

    public Project save(Project project) {
        return mongoTemplate.save(project);
    }

    public ProjectPageResult findAll(ProjectFilterRequest request) {
        if (request.getSize() <= 0) {
            throw new IllegalArgumentException("Page size must be greater than 0");
        }
        if (request.getSortBy() == ProjectSortBy.START_DATE || request.getSortBy() == ProjectSortBy.END_DATE) {

            return findAllByNullableDate(request);
        }

        return findAllNormal(request);
    }

    private ProjectPageResult findAllNormal(ProjectFilterRequest request) {

        List<Criteria> filters = buildFilters(request);

        if (request.getCursor() != null && !request.getCursor().isBlank()) {

            ProjectCursor cursor = projectCursorUtil.decode(request.getCursor());

            filters.add(buildCursorCriteria(request.getSortBy(), request.getSortDirection(), cursor));
        }

        Criteria criteria = buildCriteria(filters);

        String sortField = getSortField(request.getSortBy());

        Sort.Direction direction = request.getSortDirection() == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;

        Query query = new Query(criteria);

        query.with(Sort.by(new Sort.Order(direction, sortField), new Sort.Order(direction, "_id")));

        query.limit(request.getSize() + 1);

        List<Project> projects = mongoTemplate.find(query, Project.class);

        return buildPageResult(projects, request);
    }

    private ProjectPageResult findAllByNullableDate(ProjectFilterRequest request) {

        List<Criteria> filters = buildFilters(request);

        if (request.getCursor() != null && !request.getCursor().isBlank()) {

            ProjectCursor cursor = projectCursorUtil.decode(request.getCursor());

            filters.add(buildCursorCriteria(request.getSortBy(), request.getSortDirection(), cursor));
        }

        Criteria criteria = buildCriteria(filters);

        String sortField = getSortField(request.getSortBy());

        Sort.Direction direction = request.getSortDirection() == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;

        Document nullFlagExpression = new Document("$cond", Arrays.asList(new Document("$eq", Arrays.asList("$" + sortField, null)), 1, 0));

        Aggregation aggregation = Aggregation.newAggregation(

                Aggregation.match(criteria),

                Aggregation.addFields().addFieldWithValue("nullFlag", nullFlagExpression).build(),

                Aggregation.sort(Sort.by(Sort.Order.asc("nullFlag"), new Sort.Order(direction, sortField), new Sort.Order(direction, "_id"))),

                Aggregation.limit((long) request.getSize() + 1));

        AggregationResults<Project> results = mongoTemplate.aggregate(aggregation, "projects", Project.class);

        List<Project> projects = results.getMappedResults();

        return buildPageResult(projects, request);
    }

    private List<Criteria> buildFilters(ProjectFilterRequest request) {

        List<Criteria> filters = new ArrayList<>();

        if (request.getStatus() != null) {

            filters.add(Criteria.where("status").is(request.getStatus()));
        }

        if (request.getMinBudget() != null) {

            filters.add(Criteria.where("finalBudget").gte(request.getMinBudget()));
        }

        if (request.getMaxBudget() != null) {

            filters.add(Criteria.where("finalBudget").lte(request.getMaxBudget()));
        }

        return filters;
    }

    private Criteria buildCriteria(List<Criteria> filters) {

        Criteria criteria = new Criteria();

        if (!filters.isEmpty()) {

            criteria.andOperator(filters.toArray(new Criteria[0]));
        }

        return criteria;
    }

    private Criteria buildCursorCriteria(ProjectSortBy sortBy, SortDirection sortDirection, ProjectCursor cursor) {

        String field = getSortField(sortBy);

        boolean ascending = sortDirection == SortDirection.ASC;

        if (cursor.getNullFlag() != null && cursor.getNullFlag() == 1) {

            Criteria idCriteria = ascending ? Criteria.where("_id").gt(cursor.getId()) : Criteria.where("_id").lt(cursor.getId());

            return new Criteria().andOperator(Criteria.where(field).is(null), idCriteria);
        }

        if (cursor.getSortValue() == null) {

            throw new IllegalArgumentException("Invalid cursor: missing sort value");
        }

        Object sortValue = parseSortValue(sortBy, cursor.getSortValue());

        Criteria valueAfterCursor = ascending ? Criteria.where(field).gt(sortValue) : Criteria.where(field).lt(sortValue);

        Criteria sameValueAfterCursor = ascending ? new Criteria().andOperator(Criteria.where(field).is(sortValue), Criteria.where("_id").gt(cursor.getId())) : new Criteria().andOperator(Criteria.where(field).is(sortValue), Criteria.where("_id").lt(cursor.getId()));

        if (sortBy == ProjectSortBy.START_DATE || sortBy == ProjectSortBy.END_DATE) {

            return new Criteria().orOperator(valueAfterCursor, sameValueAfterCursor, Criteria.where(field).is(null));
        }

        return new Criteria().orOperator(valueAfterCursor, sameValueAfterCursor);
    }

    private ProjectCursor createCursor(Project project, ProjectSortBy sortBy) {

        String sortValue;

        switch (sortBy) {

            case CREATED_AT -> sortValue = project.getCreatedAt() != null ? project.getCreatedAt().toString() : null;

            case UPDATED_AT -> sortValue = project.getUpdatedAt() != null ? project.getUpdatedAt().toString() : null;

            case START_DATE -> sortValue = project.getStartDate() != null ? project.getStartDate().toString() : null;

            case END_DATE -> sortValue = project.getEndDate() != null ? project.getEndDate().toString() : null;

            default -> throw new IllegalStateException("Unexpected sort field: " + sortBy);
        }

        int nullFlag = sortValue == null ? 1 : 0;

        return new ProjectCursor(project.getId(), sortValue, nullFlag);
    }

    private Object parseSortValue(ProjectSortBy sortBy, String sortValue) {

        return switch (sortBy) {

            case CREATED_AT, UPDATED_AT -> LocalDateTime.parse(sortValue);

            case START_DATE, END_DATE -> LocalDate.parse(sortValue);
        };
    }

    private String getSortField(ProjectSortBy sortBy) {

        return switch (sortBy) {

            case CREATED_AT -> "createdAt";

            case UPDATED_AT -> "updatedAt";

            case START_DATE -> "startDate";

            case END_DATE -> "endDate";
        };
    }

    private ProjectPageResult buildPageResult(List<Project> projects, ProjectFilterRequest request) {

        int size = request.getSize();

        boolean hasNext = projects.size() > size;

        if (hasNext) {
            projects = new ArrayList<>(projects.subList(0, size));
        }

        String nextCursor = null;

        if (hasNext && !projects.isEmpty()) {

            Project lastProject = projects.getLast();

            ProjectCursor cursor = createCursor(lastProject, request.getSortBy());

            nextCursor = projectCursorUtil.encode(cursor);
        }

        return ProjectPageResult.builder().projects(projects).nextCursor(nextCursor).hasNext(hasNext).build();
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
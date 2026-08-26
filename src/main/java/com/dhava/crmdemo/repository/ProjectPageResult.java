package com.dhava.crmdemo.repository;

import com.dhava.crmdemo.entity.Project;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProjectPageResult {

    private List<Project> projects;

    private String nextCursor;

    private boolean hasNext;
}
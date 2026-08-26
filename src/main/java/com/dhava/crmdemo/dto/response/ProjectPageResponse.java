package com.dhava.crmdemo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProjectPageResponse {

    private List<ProjectResponse> content;

    private Integer pageSize;

    private String nextCursor;

    private boolean hasNext;
}

package com.dhava.crmdemo.util;

import com.dhava.crmdemo.dto.cursor.ProjectCursor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class ProjectCursorUtil {

    private final ObjectMapper objectMapper;

    public String encode(ProjectCursor cursor) {

        try {
            String json = objectMapper.writeValueAsString(cursor);

            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(json.getBytes(StandardCharsets.UTF_8));

        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to encode cursor", e);
        }
    }

    public ProjectCursor decode(String cursor) {

        try {
            byte[] decoded = Base64.getUrlDecoder().decode(cursor);

            String json = new String(decoded, StandardCharsets.UTF_8);

            return objectMapper.readValue(json, ProjectCursor.class);

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid cursor");
        }
    }
}
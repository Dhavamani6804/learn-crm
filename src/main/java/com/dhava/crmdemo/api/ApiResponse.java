package com.dhava.crmdemo.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * Standardized API response structure for the application.
 *
 * @param <T> the type of the data payload
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "timestamp",
        "status",
        "statusCode",
        "message",
        "data",
        "errors"
})
public class ApiResponse<T> {

    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS"
    )
    private final LocalDateTime timestamp = LocalDateTime.now();

    private final HttpStatus status;

    private final int statusCode;

    private final String message;

    private final T data;

    private final Object errors;

    public ApiResponse(
            HttpStatus status,
            String message,
            T data,
            Object errors
    ) {
        this.status = status;
        this.statusCode = status.value();
        this.message = message;
        this.data = data;
        this.errors = errors;
    }

    public static <T> ApiResponse<T> success(
            HttpStatus status,
            String message,
            T data
    ) {
        return new ApiResponse<>(status, message, data, null);
    }

    public static <T> ApiResponse<T> ok(
            T data,
            String message
    ) {
        return success(HttpStatus.OK, message, data);
    }

    public static <T> ApiResponse<T> created(String message) {
        return new ApiResponse<>(
                HttpStatus.CREATED,
                message,
                null,
                null
        );
    }

    public static <T> ApiResponse<T> created(T data, String message) {
        return new ApiResponse<>(
                HttpStatus.CREATED,
                message,
                data,
                null
        );
    }

    public static <T> ApiResponse<T> error(
            HttpStatus status,
            String message,
            Object errors
    ) {
        return new ApiResponse<>(
                status,
                message,
                null,
                errors
        );
    }

    public static <T> ApiResponse<T> noContent(String message) {
        return new ApiResponse<>(
                HttpStatus.NO_CONTENT,
                message,
                null,
                null
        );
    }

    public static <T> ApiResponse<T> badRequest(
            String message,
            Object errors
    ) {
        return new ApiResponse<>(
                HttpStatus.BAD_REQUEST,
                message,
                null,
                errors
        );
    }

    public static <T> ApiResponse<T> unauthorized(
            String message,
            Object errors
    ) {
        return new ApiResponse<>(
                HttpStatus.UNAUTHORIZED,
                message,
                null,
                errors
        );
    }

    public static <T> ApiResponse<T> forbidden(
            String message,
            Object errors
    ) {
        return new ApiResponse<>(
                HttpStatus.FORBIDDEN,
                message,
                null,
                errors
        );
    }

    public static <T> ApiResponse<T> notFound(
            String message,
            Object errors
    ) {
        return new ApiResponse<>(
                HttpStatus.NOT_FOUND,
                message,
                null,
                errors
        );
    }

    public static <T> ApiResponse<T> conflict(
            String message,
            Object errors
    ) {
        return new ApiResponse<>(
                HttpStatus.CONFLICT,
                message,
                null,
                errors
        );
    }

    public static <T> ApiResponse<T> unprocessableEntity(
            String message,
            Object errors
    ) {
        return new ApiResponse<>(
                HttpStatus.UNPROCESSABLE_ENTITY,
                message,
                null,
                errors
        );
    }

    public static <T> ApiResponse<T> internalServerError(
            String message,
            Object errors
    ) {
        return new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR,
                message,
                null,
                errors
        );
    }
}

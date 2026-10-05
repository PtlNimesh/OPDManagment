package com.OPD.OPDManagment.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class ValidationExceptionResponseDto {

    private int status;
    private String path;
    private String error;
    private LocalDateTime timestamp;
    private Map<String, String> message;
    private Map<String, String> fieldErrors;

    public ValidationExceptionResponseDto() {
    }

    public ValidationExceptionResponseDto(int status, String path, String error, LocalDateTime timestamp, Map<String, String> message) {
        this.status = status;
        this.path = path;
        this.error = error;
        this.timestamp = timestamp;
        this.message = message;
        this.fieldErrors = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, String> getMessage() {
        return message;
    }

    public void setMessage(Map<String, String> message) {
        this.message = message;
        this.fieldErrors = message;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(Map<String, String> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }
}

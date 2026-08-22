package com.dhava.crmdemo.exception;

public class LeadAlreadyExistException extends RuntimeException {
    public LeadAlreadyExistException(String message) {
        super(message);
    }
}

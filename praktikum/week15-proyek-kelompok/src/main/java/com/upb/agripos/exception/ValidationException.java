package com.upb.agripos.exception;

/**
 * ValidationException - Custom exception untuk business logic validation errors
 * 
 * Thrown when: Data validation fails (invalid input, business rule violation)
 * 
 * Created by: [Person B]
 * Last modified: 
 */
public class ValidationException extends Exception {
    
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}

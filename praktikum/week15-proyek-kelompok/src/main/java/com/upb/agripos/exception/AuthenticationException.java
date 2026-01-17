package com.upb.agripos.exception;

/**
 * AuthenticationException - Custom exception untuk authentication failures
 * 
 * Thrown when: Login fails, invalid credentials, user not found
 * 
 * Created by: [Person D]
 * Last modified: 
 */
public class AuthenticationException extends Exception {
    
    public AuthenticationException(String message) {
        super(message);
    }
    
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}

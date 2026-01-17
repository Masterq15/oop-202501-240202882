package com.upb.agripos.exception;

/**
 * DatabaseException - Custom exception untuk database operation errors
 * 
 * Thrown when: JDBC operations fail, connection errors, SQL errors
 * 
 * Created by: [Person A]
 * Last modified: 
 */
public class DatabaseException extends Exception {
    
    public DatabaseException(String message) {
        super(message);
    }
    
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.tummytrek.util;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

public class ValidationUtil {
    
    public static ResponseEntity<Map<String, String>> handleValidationErrors(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        
        for (FieldError error : result.getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        
        return ResponseEntity.badRequest().body(errors);
    }
    
    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
    
    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) return false;
        return phoneNumber.matches("^\\+?[1-9]\\d{1,14}$");
    }
    
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    public static void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid ID provided");
        }
    }
    
    public static String sanitizeSearchTerm(String searchTerm) {
        if (isNullOrEmpty(searchTerm)) {
            throw new IllegalArgumentException("Search term cannot be null or empty");
        }
        
        // Remove any potential SQL injection characters and trim whitespace
        String sanitized = searchTerm.trim()
            .replaceAll("[;'\"\\\\]", "")
            .replaceAll("\\s+", " ");
            
        if (sanitized.length() < 2) {
            throw new IllegalArgumentException("Search term must be at least 2 characters long");
        }
        
        if (sanitized.length() > 100) {
            throw new IllegalArgumentException("Search term cannot exceed 100 characters");
        }
        
        return sanitized;
    }
}

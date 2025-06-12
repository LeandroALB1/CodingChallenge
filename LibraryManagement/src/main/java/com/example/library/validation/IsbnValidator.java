package com.example.library.validation;

import org.springframework.stereotype.Component;

@Component
public class IsbnValidator {
    private static final String ISBN_REGEX = "\\d{13}";

    public void validate(String isbn) {
        if (isbn == null || !isbn.matches(ISBN_REGEX)) {
            throw new IllegalArgumentException("Error isbn number is in wrong format.");
        }
    }
}
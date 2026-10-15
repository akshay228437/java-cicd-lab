package com.example.cicdlab;

import org.springframework.stereotype.Service;

@Service
public class GreetingService {

    public static final int MAX_NAME_LENGTH = 100;

    public String greet(String name) {
        String normalizedName = name == null ? "" : name.strip();

        if (normalizedName.isEmpty()) {
            normalizedName = "World";
        }

        if (normalizedName.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("Name must be 100 characters or fewer.");
        }

        return "Hello, " + normalizedName + "!";
    }
}

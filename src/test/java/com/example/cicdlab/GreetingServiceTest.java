package com.example.cicdlab;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GreetingServiceTest {

    private final GreetingService service = new GreetingService();

    @Test
    void greetsTheRequestedPerson() {
        assertEquals("Hello, Jenkins!", service.greet("Jenkins"));
    }

    @Test
    void stripsSurroundingWhitespace() {
        assertEquals("Hello, Jenkins!", service.greet("  Jenkins  "));
    }

    @Test
    void defaultsMissingAndBlankNames() {
        assertEquals("Hello, World!", service.greet(null));
        assertEquals("Hello, World!", service.greet(""));
        assertEquals("Hello, World!", service.greet(" \t\n"));
    }

    @Test
    void acceptsANameAtTheLengthLimit() {
        String name = "a".repeat(GreetingService.MAX_NAME_LENGTH);
        assertEquals("Hello, " + name + "!", service.greet(name));
    }

    @Test
    void rejectsNamesBeyondTheLengthLimit() {
        String name = "a".repeat(GreetingService.MAX_NAME_LENGTH + 1);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> service.greet(name));

        assertEquals("Name must be 100 characters or fewer.", exception.getMessage());
    }
}

package com.bookings.requests;

import jakarta.validation.*;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserRequestTest {

    private final Validator validator;

    public UserRequestTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidUserRequest() {
        UserRequest request = new UserRequest();
        request.setName("Neha Sharma");
        request.setEmail("neha@example.com");
        request.setPassword("Neha@1234");

        Set<ConstraintViolation<UserRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testInvalidEmail() {
        UserRequest request = new UserRequest();
        request.setName("Neha");
        request.setEmail("bad-email");
        request.setPassword("Neha@1234");

        Set<ConstraintViolation<UserRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());

        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Email is invalid")));
    }

    @Test
    void testWeakPassword() {
        UserRequest request = new UserRequest();
        request.setName("Neha");
        request.setEmail("neha@example.com");
        request.setPassword("weak");

        Set<ConstraintViolation<UserRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void testMissingName() {
        UserRequest request = new UserRequest();
        request.setEmail("neha@example.com");
        request.setPassword("Neha@1234");

        Set<ConstraintViolation<UserRequest>> violations = validator.validate(request);

        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Name is required")));
    }
}

package com.bookings.requests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LoginRequestTest {

    @Test
    void testLoginRequestSettersAndGetters() {
        LoginRequest request = new LoginRequest();

        request.setEmail("neha@example.com");
        request.setPassword("Neha@123");

        assertEquals("neha@example.com", request.getEmail());
        assertEquals("Neha@123", request.getPassword());
    }
}

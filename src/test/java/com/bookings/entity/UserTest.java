package com.bookings.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class UserTest {

    @Test
    void testUserEntityProperties() {
        User user = new User();

        user.setId("U100");
        user.setName("Neha Sharma");
        user.setEmail("neha@example.com");
        user.setPassword("Neha@1234");
        user.setRole(Role.USER);

        assertEquals("U100", user.getId());
        assertEquals("Neha Sharma", user.getName());
        assertEquals("neha@example.com", user.getEmail());
        assertEquals("Neha@1234", user.getPassword());
        assertEquals(Role.USER, user.getRole());
    }
}

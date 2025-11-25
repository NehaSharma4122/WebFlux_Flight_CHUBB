package com.bookings.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class PassengerTest {

    @Test
    void testPassengerEntityProperties() {
        Passenger passenger = new Passenger();

        passenger.setId("P001");
        passenger.setName("Neha");
        passenger.setGender("Female");
        passenger.setAge(22);

        assertEquals("P001", passenger.getId());
        assertEquals("Neha", passenger.getName());
        assertEquals("Female", passenger.getGender());
        assertEquals(22, passenger.getAge());
    }
}

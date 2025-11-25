package com.bookings.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class FlightTest {

    @Test
    void testFlightEntitySettersAndGetters() {
        Flight flight = new Flight();

        flight.setId("F001");
        flight.setAirline_name("IndiGo");
        flight.setAirline_logo("logo.png");
        flight.setFlightNumber("6E-501");
        flight.setFromPlace("DEL");
        flight.setToPlace("BLR");

        LocalDateTime departure = LocalDateTime.now().plusDays(1);
        LocalDateTime arrival = LocalDateTime.now().plusDays(1).plusHours(3);

        flight.setDeparture(departure);
        flight.setArrival(arrival);
        flight.setPrice(5000);
        flight.setAvailableSeats(180);
        flight.setMealType(MealType.BOTH);

        assertEquals("F001", flight.getId());
        assertEquals("IndiGo", flight.getAirline_name());
        assertEquals("logo.png", flight.getAirline_logo());
        assertEquals("6E-501", flight.getFlightNumber());
        assertEquals("DEL", flight.getFromPlace());
        assertEquals("BLR", flight.getToPlace());
        assertEquals(departure, flight.getDeparture());
        assertEquals(arrival, flight.getArrival());
        assertEquals(5000, flight.getPrice());
        assertEquals(180, flight.getAvailableSeats());
        assertEquals(MealType.BOTH, flight.getMealType());
    }
}

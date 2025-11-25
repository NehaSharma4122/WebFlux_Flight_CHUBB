package com.bookings.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

public class TicketTest {

    @Test
    void testTicketEntityProperties() {
        Ticket ticket = new Ticket();

        ticket.setPnr("PNR12345");
        ticket.setUserId("U001");
        ticket.setFlightId("F001");
        ticket.setNumSeats(2);

        Passenger p1 = new Passenger();
        p1.setName("Neha");

        Passenger p2 = new Passenger();
        p2.setName("Ananya");

        ticket.setPassenger(List.of(p1, p2));

        ticket.setMealpref(MealType.VEG);
        ticket.setSeatNumber("12A");

        LocalDateTime now = LocalDateTime.now();
        ticket.setBookingDate(now);
        ticket.setStatus("CONFIRMED");

        assertEquals("PNR12345", ticket.getPnr());
        assertEquals("U001", ticket.getUserId());
        assertEquals("F001", ticket.getFlightId());
        assertEquals(2, ticket.getNumSeats());
        assertEquals("Neha", ticket.getPassenger().get(0).getName());
        assertEquals(MealType.VEG, ticket.getMealpref());
        assertEquals("12A", ticket.getSeatNumber());
        assertEquals(now, ticket.getBookingDate());
        assertEquals("CONFIRMED", ticket.getStatus());
    }
}

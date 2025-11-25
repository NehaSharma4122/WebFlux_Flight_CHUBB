package com.bookings.service;

import com.bookings.entity.*;
import com.bookings.exception.*;
import com.bookings.repository.*;
import com.bookings.requests.BookingRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

class BookingServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Flight flight;
    private User user;
    private BookingRequest bookingRequest;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        flight = new Flight();
        flight.setId("F001");
        flight.setAvailableSeats(10);
        flight.setDeparture(LocalDateTime.now().plusDays(2));

        user = new User();
        user.setId("U001");
        user.setName("Neha");
        user.setEmail("neha@example.com");

        Passenger p = new Passenger();
        p.setName("Neha");
        p.setAge(22);

        bookingRequest = new BookingRequest();
        bookingRequest.setName("Neha");
        bookingRequest.setEmail("neha@example.com");
        bookingRequest.setTotalSeats(2);
        bookingRequest.setPassenger(List.of(p));
    }

    @Test
    void testBookFlightSuccess() {
        when(flightRepository.findById("F001")).thenReturn(Mono.just(flight));
        when(userRepository.findByEmail("neha@example.com")).thenReturn(Mono.just(user));
        when(flightRepository.save(any())).thenReturn(Mono.just(flight));
        when(ticketRepository.save(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(bookingService.bookFlight("F001", bookingRequest))
                .expectNextMatches(ticket -> ticket.getNumSeats() == 2)
                .verifyComplete();
    }

    @Test
    void testBookFlightNotEnoughSeats() {
        flight.setAvailableSeats(1); // not enough

        when(flightRepository.findById("F001")).thenReturn(Mono.just(flight));
        when(userRepository.findByEmail(anyString())).thenReturn(Mono.just(user));

        StepVerifier.create(bookingService.bookFlight("F001", bookingRequest))
                .expectErrorMatches(ex -> ex instanceof UnprocessableException &&
                        ex.getMessage().contains("Not enough seats"))
                .verify();
    }

    @Test
    void testBookFlightAutoCreateUser() {
        when(flightRepository.findById("F001")).thenReturn(Mono.just(flight));
        when(userRepository.findByEmail(anyString())).thenReturn(Mono.empty());
        when(userRepository.save(any())).thenReturn(Mono.just(user));
        when(flightRepository.save(any())).thenReturn(Mono.just(flight));
        when(ticketRepository.save(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(bookingService.bookFlight("F001", bookingRequest))
                .expectNextMatches(ticket -> ticket.getUserId().equals("U001"))
                .verifyComplete();
    }

    @Test
    void testGetTicketByPnr() {
        Ticket t = new Ticket();
        t.setPnr("PNR001");

        when(ticketRepository.findByPnr("PNR001")).thenReturn(Mono.just(t));

        StepVerifier.create(bookingService.getTicketByPnr("PNR001"))
                .expectNext(t)
                .verifyComplete();
    }

    @Test
    void testCancelTicketSuccess() {
        Ticket ticket = new Ticket();
        ticket.setFlightId("F001");
        ticket.setNumSeats(2);
        ticket.setPnr("PNR001");

        when(ticketRepository.findByPnr("PNR001")).thenReturn(Mono.just(ticket));
        when(flightRepository.findById("F001")).thenReturn(Mono.just(flight));
        when(flightRepository.save(any())).thenReturn(Mono.just(flight));
        when(ticketRepository.save(any())).thenReturn(Mono.just(ticket));

        StepVerifier.create(bookingService.cancelTicket("PNR001"))
                .expectNext("Ticket cancelled successfully")
                .verifyComplete();
    }

    @Test
    void testCancelTicketTooLate() {
        Ticket ticket = new Ticket();
        ticket.setFlightId("F001");
        flight.setDeparture(LocalDateTime.now().plusHours(10)); // too close

        when(ticketRepository.findByPnr(anyString())).thenReturn(Mono.just(ticket));
        when(flightRepository.findById("F001")).thenReturn(Mono.just(flight));

        StepVerifier.create(bookingService.cancelTicket("PNR001"))
                .expectError(UnprocessableException.class)
                .verify();
    }
}

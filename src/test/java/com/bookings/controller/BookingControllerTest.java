package com.bookings.controller;

import com.bookings.entity.*;
import com.bookings.exception.*;
import com.bookings.requests.BookingRequest;
import com.bookings.service.BookingService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@WebFluxTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private BookingService bookingService;

    @Test
    void testBookFlightSuccess() {

        Ticket ticket = new Ticket();
        ticket.setPnr("PNR001");

        BookingRequest req = new BookingRequest();
        req.setName("Neha");
        req.setEmail("neha@example.com");
        req.setTotalSeats(2);
        req.setPassenger(List.of());

        Mockito.when(bookingService.bookFlight(Mockito.anyString(), Mockito.any()))
                .thenReturn(Mono.just(ticket));

        webTestClient.post()
                .uri("/api/v1.0/flight/booking/123")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.pnr").isEqualTo("PNR001");
    }

    @Test
    void testBookFlightNotEnoughSeats() {

        BookingRequest req = new BookingRequest();
        req.setName("Neha");
        req.setEmail("neha@example.com");
        req.setTotalSeats(2);

        Passenger p = new Passenger();
        p.setName("Test");
        p.setGender("F");
        p.setAge(22);

        req.setPassenger(List.of(p));

        Mockito.when(bookingService.bookFlight(Mockito.anyString(), Mockito.any()))
                .thenReturn(Mono.error(new UnprocessableException("Not enough seats")));

        webTestClient.post()
                .uri("/api/v1.0/flight/booking/123")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isEqualTo(422);
    }

    @Test
    void testGetTicket() {

        Ticket t = new Ticket();
        t.setPnr("PNR001");

        Mockito.when(bookingService.getTicketByPnr("PNR001"))
                .thenReturn(Mono.just(t));

        webTestClient.get()
                .uri("/api/v1.0/flight/ticket/PNR001")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.pnr").isEqualTo("PNR001");
    }

    @Test
    void testGetTicketNotFound() {

        Mockito.when(bookingService.getTicketByPnr("X"))
                .thenReturn(Mono.error(new ResourceNotFoundException("Not found")));

        webTestClient.get()
                .uri("/api/v1.0/flight/ticket/X")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testCancelTicket() {

        Mockito.when(bookingService.cancelTicket("PNR001"))
                .thenReturn(Mono.just("Ticket cancelled successfully"));

        webTestClient.delete()
                .uri("/api/v1.0/flight/booking/cancel/PNR001")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Ticket cancelled successfully");
    }
}

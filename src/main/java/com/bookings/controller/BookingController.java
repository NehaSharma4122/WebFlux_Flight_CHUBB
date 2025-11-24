package com.bookings.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookings.entity.Ticket;
import com.bookings.requests.BookingRequest;
import com.bookings.service.BookingService;

import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1.0/flight")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @PostMapping("/booking/{flightId}")
    public Mono<ResponseEntity<Ticket>> bookFlight(
            @PathVariable String flightId,
            @Valid @RequestBody BookingRequest bookingRequest) {

        return bookingService.bookFlight(flightId, bookingRequest)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/ticket/{pnr}")
    public Mono<ResponseEntity<Ticket>> getTicket(@PathVariable String pnr) {
        return bookingService.getTicketByPnr(pnr)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/booking/history/{emailId}")
    public Flux<Ticket> getBookingHistory(@PathVariable String emailId) {
        return bookingService.getBookingHistory(emailId);
    }

    @DeleteMapping("/booking/cancel/{pnr}")
    public Mono<ResponseEntity<String>> cancelTicket(@PathVariable String pnr) {
        return bookingService.cancelTicket(pnr)
                .map(ResponseEntity::ok);
    }
}
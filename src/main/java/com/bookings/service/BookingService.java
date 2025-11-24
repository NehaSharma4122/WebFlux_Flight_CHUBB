package com.bookings.service;

import com.bookings.entity.Ticket;
import com.bookings.requests.BookingRequest;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BookingService {
	Mono<Ticket> bookFlight(String flightId, BookingRequest bookingRequest);
	Mono<Ticket> getTicketByPnr(String pnr);
    Flux<Ticket> getBookingHistory(String email);
    Mono<String> cancelTicket(String pnr);
}

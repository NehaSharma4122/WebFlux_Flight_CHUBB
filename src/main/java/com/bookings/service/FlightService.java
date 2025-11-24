package com.bookings.service;

import org.springframework.stereotype.Service;

import com.bookings.entity.Flight;
import com.bookings.requests.SearchRequests;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public interface FlightService {
	Mono<Flight> addFlight(Flight flight);
    Flux<Flight> searchFlights(SearchRequests searchRequest);
    Mono<Flight> getFlightById(String id);
    Flux<Flight> getAllFlights();
}

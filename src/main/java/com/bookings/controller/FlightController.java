package com.bookings.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookings.entity.Flight;
import com.bookings.requests.SearchRequests;
import com.bookings.service.FlightService;

import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1.0/flight")
public class FlightController {

    @Autowired
    private FlightService flightService;

    @PostMapping("/airline/inventory/add")
    public Mono<ResponseEntity<Flight>> addFlight(@Valid @RequestBody Flight flight) {
        return flightService.addFlight(flight)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/search")
    public Flux<Flight> searchFlights(@Valid @RequestBody SearchRequests searchRequest) {
        return flightService.searchFlights(searchRequest);
    }

    @GetMapping("/airline/inventory/all")
    public Flux<Flight> getAllFlights() {
        return flightService.getAllFlights();
    }
}
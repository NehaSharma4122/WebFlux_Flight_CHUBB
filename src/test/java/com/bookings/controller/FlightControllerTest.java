package com.bookings.controller;

import com.bookings.entity.Flight;
import com.bookings.requests.SearchRequests;
import com.bookings.requests.Trip;
import com.bookings.service.FlightService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;

@WebFluxTest(controllers = FlightController.class)
class FlightControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private FlightService flightService;

    @Test
    void testAddFlight() {

        Flight flight = new Flight();
        flight.setId("F1");
        flight.setAirline_name("Air India");
        flight.setAirline_logo("logo.png");
        flight.setFlightNumber("AI101");
        flight.setFromPlace("DEL");
        flight.setToPlace("BLR");
        flight.setDeparture(LocalDateTime.now().plusDays(1));
        flight.setArrival(LocalDateTime.now().plusDays(1).plusHours(2));
        flight.setPrice(5000);
        flight.setAvailableSeats(150);

        Mockito.when(flightService.addFlight(Mockito.any()))
                .thenReturn(Mono.just(flight));

        webTestClient.post()
                .uri("/api/v1.0/flight/airline/inventory/add")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(flight)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("F1");
    }

    @Test
    void testSearchFlights() {

        Flight f = new Flight();
        f.setId("F1");

        Mockito.when(flightService.searchFlights(Mockito.any()))
                .thenReturn(Flux.just(f));

        SearchRequests req = new SearchRequests();
        req.setFromPlace("DEL");
        req.setToPlace("BLR");
        req.setTripType(Trip.ONE_WAY);
        req.setTravelDate(LocalDate.now().plusDays(1));

        webTestClient.post()
                .uri("/api/v1.0/flight/search")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo("F1");
    }

    @Test
    void testGetAllFlights() {

        Flight f = new Flight();
        f.setId("F1");

        Mockito.when(flightService.getAllFlights())
                .thenReturn(Flux.just(f));

        webTestClient.get()
                .uri("/api/v1.0/flight/airline/inventory/all")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo("F1");
    }
}


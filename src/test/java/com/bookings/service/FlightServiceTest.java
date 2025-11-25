package com.bookings.service;

import com.bookings.entity.Flight;
import com.bookings.exception.ResourceNotFoundException;
import com.bookings.exception.UnprocessableException;
import com.bookings.repository.FlightRepository;
import com.bookings.requests.SearchRequests;
import com.bookings.requests.Trip;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class FlightServiceTest {

    @Mock
    private FlightRepository flightRepository;

    @InjectMocks
    private FlightServiceImpl flightService;

    // ------------------------------------------------------------
    // 1. addFlight()
    // ------------------------------------------------------------
    @Test
    void testAddFlight() {
        Flight f = new Flight();
        f.setId("F1");

        Mockito.when(flightRepository.save(any())).thenReturn(Mono.just(f));

        Mono<Flight> result = flightService.addFlight(f);

        assertEquals("F1", result.block().getId());
    }

    // ------------------------------------------------------------
    // 2. searchFlights() - ONE WAY
    // ------------------------------------------------------------
    @Test
    void testSearchOneWay() {
        SearchRequests req = new SearchRequests();
        req.setFromPlace("DEL");
        req.setToPlace("BLR");
        req.setTripType(Trip.ONE_WAY);
        req.setTravelDate(LocalDate.now().plusDays(1));

        Flight f = new Flight();
        f.setId("F1");

        Mockito.when(flightRepository.findByFromPlaceAndToPlaceAndDepartureBetween(
                anyString(), anyString(), any(), any()))
                .thenReturn(Flux.just(f));

        Flux<Flight> result = flightService.searchFlights(req);

        assertEquals("F1", result.blockFirst().getId());
    }

    // ------------------------------------------------------------
    // 3. searchFlights() - ROUND TRIP SUCCESS
    // ------------------------------------------------------------
    @Test
    void testSearchRoundTripSuccess() {
        SearchRequests req = new SearchRequests();
        req.setFromPlace("DEL");
        req.setToPlace("BLR");
        req.setTripType(Trip.ROUND_TRIP);
        req.setTravelDate(LocalDate.now().plusDays(1));
        req.setReturnDate(LocalDate.now().plusDays(3));

        Flight outbound = new Flight();
        outbound.setId("OB");

        Flight inbound = new Flight();
        inbound.setId("IB");

        Mockito.when(flightRepository.findByFromPlaceAndToPlaceAndDepartureBetween(
                anyString(), anyString(), any(), any()))
                .thenReturn(Flux.just(outbound))  // first call: outbound
                .thenReturn(Flux.just(inbound));  // second call: inbound

        Flux<Flight> result = flightService.searchFlights(req);

        // first element should be outbound
        assertEquals("OB", result.blockFirst().getId());
    }

    // ------------------------------------------------------------
    // 4. searchFlights() - ROUND TRIP missing return date
    // ------------------------------------------------------------
    @Test
    void testRoundTripMissingReturnDate() {
        SearchRequests req = new SearchRequests();
        req.setTripType(Trip.ROUND_TRIP);
        req.setTravelDate(LocalDate.now().plusDays(1));

        assertThrows(UnprocessableException.class,
                () -> flightService.searchFlights(req).blockFirst());
    }

    // ------------------------------------------------------------
    // 5. searchFlights() - ROUND TRIP invalid return date
    // ------------------------------------------------------------
    @Test
    void testRoundTripInvalidReturnDate() {
        SearchRequests req = new SearchRequests();
        req.setTripType(Trip.ROUND_TRIP);
        req.setTravelDate(LocalDate.now().plusDays(3));
        req.setReturnDate(LocalDate.now().plusDays(1)); // return < departure

        assertThrows(UnprocessableException.class,
                () -> flightService.searchFlights(req).blockFirst());
    }

    // ------------------------------------------------------------
    // 6. searchFlights() - DEFAULT CASE (tripType null)
    // ------------------------------------------------------------
    @Test
    void testSearchDefaultCase() {
        SearchRequests req = new SearchRequests();
        req.setTripType(null); // goes to final return branch
        req.setFromPlace("DEL");
        req.setToPlace("BLR");
        req.setTravelDate(LocalDate.now().plusDays(1));

        Flight f = new Flight();
        f.setId("DEF");

        Mockito.when(flightRepository.findByFromPlaceAndToPlaceAndDepartureBetween(
                anyString(), anyString(), any(), any()))
                .thenReturn(Flux.just(f));

        Flux<Flight> result = flightService.searchFlights(req);

        assertEquals("DEF", result.blockFirst().getId());
    }

    // ------------------------------------------------------------
    // 7. getFlightById() success
    // ------------------------------------------------------------
    @Test
    void testGetFlightByIdSuccess() {
        Flight f = new Flight();
        f.setId("F1");

        Mockito.when(flightRepository.findById("F1")).thenReturn(Mono.just(f));

        assertEquals("F1", flightService.getFlightById("F1").block().getId());
    }

    // ------------------------------------------------------------
    // 8. getFlightById() NOT FOUND
    // ------------------------------------------------------------
    @Test
    void testGetFlightByIdNotFound() {
        Mockito.when(flightRepository.findById("X")).thenReturn(Mono.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> flightService.getFlightById("X").block());
    }

    // ------------------------------------------------------------
    // 9. getAllFlights()
    // ------------------------------------------------------------
    @Test
    void testGetAllFlights() {
        Flight f = new Flight();
        f.setId("A1");

        Mockito.when(flightRepository.findAll()).thenReturn(Flux.just(f));

        assertEquals("A1", flightService.getAllFlights().blockFirst().getId());
    }
}

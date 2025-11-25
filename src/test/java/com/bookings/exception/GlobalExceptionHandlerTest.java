package com.bookings.exception;

import com.bookings.controller.BookingController;
import com.bookings.requests.BookingRequest;
import com.bookings.service.BookingService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;

import reactor.core.publisher.Mono;

@WebFluxTest(controllers = BookingController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private BookingService bookingService;

    // -------------------------------------------------------------------
    // Helper: Valid JSON Body (prevents 400 validation errors)
    // -------------------------------------------------------------------
    private static final String VALID_BOOKING_JSON = """
    {
      "name": "Neha",
      "email": "neha@example.com",
      "totalSeats": 1,
      "passenger": [
        {"name": "A", "gender": "Female", "age": 22}
      ]
    }
    """;

    // -------------------------------------------------------------------
    // 409 CONFLICT
    // -------------------------------------------------------------------
    @Test
    void testHandleConflict() {

        Mockito.when(bookingService.bookFlight(Mockito.anyString(), Mockito.any()))
                .thenReturn(Mono.error(new ResourceConflictException("User already exists")));

        webTestClient.post()
                .uri("/api/v1.0/flight/booking/123")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(VALID_BOOKING_JSON)
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.message").isEqualTo("User already exists")
                .jsonPath("$.exceptionType").isEqualTo("ResourceConflictException");
    }

    // -------------------------------------------------------------------
    // 422 UNPROCESSABLE ENTITY
    // -------------------------------------------------------------------
    @Test
    void testHandleUnprocessable() {

        Mockito.when(bookingService.bookFlight(Mockito.anyString(), Mockito.any()))
                .thenReturn(Mono.error(new UnprocessableException("Seat limit exceeded")));

        webTestClient.post()
                .uri("/api/v1.0/flight/booking/123")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(VALID_BOOKING_JSON)
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Seat limit exceeded")
                .jsonPath("$.exceptionType").isEqualTo("UnprocessableException");
    }

    // -------------------------------------------------------------------
    // 404 NOT FOUND
    // -------------------------------------------------------------------
    @Test
    void testHandleNotFound() {

        Mockito.when(bookingService.getTicketByPnr("PNR404"))
                .thenReturn(Mono.error(new ResourceNotFoundException("Ticket not found")));

        webTestClient.get()
                .uri("/api/v1.0/flight/ticket/PNR404")
                .exchange()
                .expectStatus().isEqualTo(404)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Ticket not found")
                .jsonPath("$.exceptionType").isEqualTo("ResourceNotFoundException");
    }

    // -------------------------------------------------------------------
    // 401 UNAUTHORIZED
    // -------------------------------------------------------------------
    @Test
    void testHandleUnauthorized() {

        Mockito.when(bookingService.getTicketByPnr("AUTH"))
                .thenReturn(Mono.error(new UnauthorizedException("Unauthorized access")));

        webTestClient.get()
                .uri("/api/v1.0/flight/ticket/AUTH")
                .exchange()
                .expectStatus().isEqualTo(401)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Unauthorized access")
                .jsonPath("$.exceptionType").isEqualTo("UnauthorizedException");
    }

    // -------------------------------------------------------------------
    // 403 FORBIDDEN
    // -------------------------------------------------------------------
    @Test
    void testHandleForbidden() {

        Mockito.when(bookingService.getTicketByPnr("FORBID"))
                .thenReturn(Mono.error(new ForbiddenException("Access denied")));

        webTestClient.get()
                .uri("/api/v1.0/flight/ticket/FORBID")
                .exchange()
                .expectStatus().isEqualTo(403)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Access denied")
                .jsonPath("$.exceptionType").isEqualTo("ForbiddenException");
    }

 // 400 – VALIDATION FAILURE
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleValidation(WebExchangeBindException ex) {

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validation Failed");

        Map<String, String> errors = new HashMap<>();
        ex.getFieldErrors().forEach(err ->
            errors.put(err.getField(), err.getDefaultMessage())
        );

        body.put("validationErrors", errors);
        body.put("message", "Input validation failed");

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body));
    }

    // -------------------------------------------------------------------
    // 500 INTERNAL SERVER ERROR
    // -------------------------------------------------------------------
    @Test
    void testHandleInternalServerError() {

        Mockito.when(bookingService.getTicketByPnr("ERR"))
                .thenReturn(Mono.error(new Exception("Server exploded")));

        webTestClient.get()
                .uri("/api/v1.0/flight/ticket/ERR")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Server exploded")
                .jsonPath("$.exceptionType").isEqualTo("Exception");
    }
}

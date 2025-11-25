package com.bookings.requests;

import com.bookings.entity.MealType;
import com.bookings.entity.Passenger;
import org.junit.jupiter.api.Test;

import jakarta.validation.*;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class BookingRequestTest {

    private final Validator validator;

    public BookingRequestTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidBookingRequest() {
        BookingRequest request = new BookingRequest();
        request.setName("Neha Sharma");
        request.setEmail("neha@example.com");
        request.setTotalSeats(2);
        request.setSeatNumber("12A");
        request.setMealpref(MealType.VEG);

        Passenger p1 = new Passenger();
        p1.setName("Neha");
        p1.setGender("Female");
        p1.setAge(22);

        request.setPassenger(List.of(p1));

        Set<ConstraintViolation<BookingRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testInvalidEmail() {
        BookingRequest request = new BookingRequest();
        request.setName("Neha");
        request.setEmail("invalid-email");
        request.setTotalSeats(1);
        request.setPassenger(List.of(new Passenger()));

        Set<ConstraintViolation<BookingRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());

        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Invalid email")));
    }

    @Test
    void testMissingName() {
        BookingRequest request = new BookingRequest();
        request.setEmail("neha@example.com");
        request.setTotalSeats(1);
        request.setPassenger(List.of(new Passenger()));

        Set<ConstraintViolation<BookingRequest>> violations = validator.validate(request);

        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Name is required")));
    }

    @Test
    void testSeatCountTooLow() {
        BookingRequest request = new BookingRequest();
        request.setName("Neha");
        request.setEmail("neha@example.com");
        request.setTotalSeats(0);
        request.setPassenger(List.of(new Passenger()));

        Set<ConstraintViolation<BookingRequest>> violations = validator.validate(request);

        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("At least 1 seat required")));
    }

    @Test
    void testPassengerListRequired() {
        BookingRequest request = new BookingRequest();
        request.setName("Neha");
        request.setEmail("neha@example.com");
        request.setTotalSeats(1);

        Set<ConstraintViolation<BookingRequest>> violations = validator.validate(request);

        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Passenger Details are required")));
    }
}

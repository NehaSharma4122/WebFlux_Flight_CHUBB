//package com.bookings.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//public class BookingController {
//	@Autowired
//    public BookingController(BookingService bookingService) {
//        this.bookingService = bookingService;
//    }
//
//    // Create booking
//    @PostMapping
//    public Mono<ResponseEntity<Booking>> createBooking(@RequestBody Booking booking) {
//        return bookingService.createBooking(booking)
//                .map(saved -> ResponseEntity.ok(saved));
//    }
//
//    // Get booking by id
//    @GetMapping("/{id}")
//    public Mono<ResponseEntity<Booking>> getBooking(@PathVariable String id) {
//        return bookingService.getBookingById(id)
//                .map(b -> ResponseEntity.ok(b))
//                .defaultIfEmpty(ResponseEntity.notFound().build());
//    }
//
//    // Get all bookings
//    @GetMapping
//    public Flux<Booking> getAllBookings() {
//        return bookingService.getAllBookings();
//    }
//
//    // Cancel booking
//    @DeleteMapping("/{id}")
//    public Mono<ResponseEntity<Void>> cancelBooking(@PathVariable String id) {
//        return bookingService.cancelBooking(id)
//                .map(v -> ResponseEntity.noContent().<Void>build())
//                .defaultIfEmpty(ResponseEntity.notFound().build());
//    }
//}

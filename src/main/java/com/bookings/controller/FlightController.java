//package com.bookings.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//
//import jakarta.validation.Valid;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//public class FlightController {
//	@Autowired
//    private BookingService bookingService;
//
//	@PostMapping("/booking/{flightid}")
//    public Mono<ResponseEntity<Tickets>> bookFlight(
//            @PathVariable Long flightid,
//            @Valid @RequestBody BookingRequest bookingRequest) {
//
//        return bookingService.bookFlight(flightid, bookingRequest)
//                .map(ResponseEntity::ok);
//    }
//
//    // ------------------------------
//    // GET TICKET BY PNR (Reactive)
//    // ------------------------------
//    @GetMapping("/ticket/{pnr}")
//    public Mono<ResponseEntity<Tickets>> getTicket(@PathVariable String pnr) {
//        return bookingService.getTicketByPnr(pnr)
//                .map(ResponseEntity::ok)
//                .defaultIfEmpty(ResponseEntity.notFound().build());
//    }
//
//    // ------------------------------
//    // BOOKING HISTORY (Reactive)
//    // ------------------------------
//    @GetMapping("/booking/history/{emailId}")
//    public Flux<Tickets> getBookingHistory(@PathVariable String emailId) {
//        return bookingService.getBookingHistory(emailId);
//    }
//
//    // ------------------------------
//    // CANCEL TICKET (Reactive)
//    // ------------------------------
//    @DeleteMapping("/booking/cancel/{pnr}")
//    public Mono<ResponseEntity<String>> cancelTicket(@PathVariable String pnr) {
//        return bookingService.cancelTicket(pnr)
//                .map(ResponseEntity::ok)
//                .defaultIfEmpty(ResponseEntity.notFound().build());
//    }
//}

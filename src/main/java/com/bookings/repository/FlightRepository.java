package com.bookings.repository;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.bookings.entity.Flight;

import reactor.core.publisher.Flux;

@Repository
public interface FlightRepository extends ReactiveMongoRepository<Flight, String>{
	 Flux<Flight> travellingDetails(
			 String fromPlace, String toPlace,
			 LocalDateTime departureStart, LocalDateTime departureEnd);
}

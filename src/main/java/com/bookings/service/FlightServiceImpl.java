package com.bookings.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookings.entity.Flight;
import com.bookings.repository.FlightRepository;
import com.bookings.requests.SearchRequests;
import com.bookings.requests.Trip;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class FlightServiceImpl implements FlightService {
	@Autowired
	private FlightRepository flightRepository;

	@Override
	public Mono<Flight> addFlight(Flight flight) {
		return flightRepository.save(flight);
	}

	@Override
	public Flux<Flight> searchFlights(SearchRequests searchRequest) {
		Trip tripType = searchRequest.getTripType();
		LocalDate travelDate = searchRequest.getTravelDate();

		LocalDateTime dayStart = travelDate.atStartOfDay();
		LocalDateTime dayEnd = dayStart.plusDays(1);

		if (tripType == Trip.ONE_WAY) {
			return flightRepository.travellingDetails(searchRequest.getFromPlace(), searchRequest.getToPlace(),
					dayStart, dayEnd);
		} else if (tripType == Trip.ROUND_TRIP) {
			if (searchRequest.getReturnDate() == null) {
				return Flux.error(new RuntimeException("Return date is required for round trip"));
			}
			if (searchRequest.getReturnDate().isBefore(travelDate)) {
				return Flux.error(new RuntimeException("Return date cannot be before departure date"));
			}

			LocalDateTime returnStart = searchRequest.getReturnDate().atStartOfDay();
			LocalDateTime returnEnd = returnStart.plusDays(1);

			Flux<Flight> outbound = flightRepository.travellingDetails(searchRequest.getFromPlace(),
					searchRequest.getToPlace(), dayStart, dayEnd);

			Flux<Flight> inbound = flightRepository.travellingDetails(searchRequest.getToPlace(),
					searchRequest.getFromPlace(), returnStart, returnEnd);

			return Flux.concat(outbound, inbound);
		} else {
			return flightRepository.travellingDetails(searchRequest.getFromPlace(), searchRequest.getToPlace(),
					dayStart, dayEnd);
		}
	}

	@Override
	public Mono<Flight> getFlightById(String id) {
		return flightRepository.findById(id)
				.switchIfEmpty(Mono.error(new RuntimeException("Flight not found with id: " + id)));
	}

	@Override
	public Flux<Flight> getAllFlights() {
		return flightRepository.findAll();
	}

}

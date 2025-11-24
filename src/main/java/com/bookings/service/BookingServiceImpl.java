package com.bookings.service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookings.entity.Flight;
import com.bookings.entity.Passenger;
import com.bookings.entity.Role;
import com.bookings.entity.Ticket;
import com.bookings.entity.User;
import com.bookings.repository.FlightRepository;
import com.bookings.repository.TicketRepository;
import com.bookings.repository.UserRepository;
import com.bookings.requests.BookingRequest;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BookingServiceImpl implements BookingService {
	@Autowired
	private TicketRepository ticketRepository;

	@Autowired
	private FlightRepository flightRepository;

	@Autowired
	private UserRepository userRepository;

	@Override
	public Mono<Ticket> bookFlight(String flightId, BookingRequest bookingRequest) {
		Mono<Flight> flightMono = flightRepository.findById(flightId)
				.switchIfEmpty(Mono.error(new RuntimeException("Flight not found")));

		Mono<User> userMono = userRepository.findByEmail(bookingRequest.getEmail()).switchIfEmpty(Mono.defer(() -> {
			User newUser = new User();
			newUser.setEmail(bookingRequest.getEmail());
			newUser.setName(bookingRequest.getName());
			newUser.setPassword(UUID.randomUUID().toString());
			newUser.setRole(Role.USER);
			return userRepository.save(newUser);
		}));

		return Mono.zip(flightMono, userMono).flatMap(tuple -> {
			Flight flight = tuple.getT1();
			User user = tuple.getT2();

			int requestedSeats = bookingRequest.getTotalSeats();
			if (flight.getAvailableSeats() < requestedSeats) {
				return Mono.error(new RuntimeException("Not enough seats available"));
			}

			// create ticket
			String pnr = generatePNR();
			Ticket ticket = new Ticket();
			ticket.setPnr(pnr);
			ticket.setUserId(user.getId()); 
			ticket.setFlightId(flight.getId()); 
			ticket.setNumSeats(requestedSeats);
			if (bookingRequest.getPassenger() != null) {
				ticket.setPassenger(bookingRequest.getPassenger().stream().map(this::convertToPassenger)
						.collect(Collectors.toList()));
			}
			ticket.setMealpref(bookingRequest.getMealpref());
			ticket.setSeatNumber(bookingRequest.getSeatNumber());
			ticket.setBookingDate(LocalDateTime.now());
			ticket.setStatus("CONFIRMED");

			flight.setAvailableSeats(flight.getAvailableSeats() - requestedSeats);

			return flightRepository.save(flight).then(ticketRepository.save(ticket));
		});
	}

	@Override
	public Mono<Ticket> getTicketByPnr(String pnr) {
		return ticketRepository.findByPnr(pnr)
				.switchIfEmpty(Mono.error(new RuntimeException("Ticket not found with PNR: " + pnr)));
	}

	@Override
	public Flux<Ticket> getBookingHistory(String email) {
		return userRepository.findByEmail(email)
				.switchIfEmpty(Mono.error(new RuntimeException("User not found with email: " + email)))
				.flatMapMany(user -> ticketRepository.findByUserId(user.getId()));
	}

	@Override
	public Mono<String> cancelTicket(String pnr) {
		return ticketRepository.findByPnr(pnr)
				.switchIfEmpty(Mono.error(new RuntimeException("Ticket not found with PNR: " + pnr)))
				.flatMap(ticket -> {
					return flightRepository.findById(ticket.getFlightId())
							.switchIfEmpty(Mono.error(new RuntimeException("Flight not found for ticket")))
							.flatMap(flight -> {
								LocalDateTime departureTime = flight.getDeparture();
								if (LocalDateTime.now().plusHours(24).isAfter(departureTime)) {
									return Mono.error(new RuntimeException(
											"Cancellation not allowed within 24 hours of departure"));
								}

								ticket.setStatus("CANCELLED");
								flight.setAvailableSeats(flight.getAvailableSeats() + ticket.getNumSeats());

								return flightRepository.save(flight).then(ticketRepository.save(ticket))
										.thenReturn("Ticket cancelled successfully");
							});
				});
	}
	private String generatePNR() {
		return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
	}

	private Passenger convertToPassenger(Passenger passengerReq) {
		Passenger passenger = new Passenger();
		passenger.setName(passengerReq.getName());
		passenger.setGender(passengerReq.getGender());
		passenger.setAge(passengerReq.getAge());
		return passenger;
	}
}

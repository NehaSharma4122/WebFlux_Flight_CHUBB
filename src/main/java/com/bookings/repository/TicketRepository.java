package com.bookings.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.bookings.entity.Ticket;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface TicketRepository extends ReactiveMongoRepository<Ticket, String>{
	Flux<Ticket> findByUserId(String userId);
	Mono<Ticket> findByPnr(String pnr);
}

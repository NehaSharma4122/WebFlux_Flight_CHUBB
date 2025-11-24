package com.bookings.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.bookings.entity.User;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveMongoRepository<User, String>{
	Mono<User> findByEmail(String email);
	Mono<Boolean> existsByEmail(String email); 

}

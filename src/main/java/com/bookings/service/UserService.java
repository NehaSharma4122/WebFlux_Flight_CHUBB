package com.bookings.service;

import com.bookings.entity.User;
import com.bookings.requests.LoginRequest;
import com.bookings.requests.UserRequest;

import reactor.core.publisher.Mono;

public interface UserService {
	Mono<User> registerUser(UserRequest userRequest);
	Mono<User> loginUser(LoginRequest loginRequest);
	Mono<User> getUserByEmail(String email);
}

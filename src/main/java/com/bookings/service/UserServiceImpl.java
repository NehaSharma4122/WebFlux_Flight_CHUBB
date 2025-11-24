package com.bookings.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookings.entity.Role;
import com.bookings.entity.User;
import com.bookings.repository.UserRepository;
import com.bookings.requests.UserRequest;

import reactor.core.publisher.Mono;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserRepository userRepository;

	@Override
	public Mono<User> registerUser(UserRequest userRequest) {
		return userRepository.existsByEmail(userRequest.getEmail()).flatMap(exists -> {
			if (exists) {
				return Mono.error(new RuntimeException("User already exists with email: " + userRequest.getEmail()));
			}

			User user = new User();
			user.setName(userRequest.getName());
			user.setEmail(userRequest.getEmail());
			user.setPassword(userRequest.getPassword()); 
			user.setRole(Role.USER);

			return userRepository.save(user);
		});
	}

	@Override
	public Mono<User> loginUser(String email, String password) {

		return userRepository.findByEmail(email).switchIfEmpty(Mono.error(new RuntimeException("User not found!")))
				.flatMap(user -> {
					if (!user.getPassword().equals(password)) {
						return Mono.error(new RuntimeException("Invalid Password"));
					}
					return Mono.just(user);
				});
	}

	@Override
	public Mono<User> getUserByEmail(String email) {
		return userRepository.findByEmail(email).switchIfEmpty(Mono.error(new RuntimeException("User not found!")));
	}
}

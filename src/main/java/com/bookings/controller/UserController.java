package com.bookings.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bookings.entity.User;
import com.bookings.requests.LoginRequest;
import com.bookings.requests.UserRequest;
import com.bookings.service.UserService;

import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1.0/flight/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Mono<ResponseEntity<User>> register(@Valid @RequestBody UserRequest userRequest) {
        return userService.registerUser(userRequest)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<User>> login(@RequestBody LoginRequest request) {
    	return userService.loginUser(request)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{email}")
    public Mono<ResponseEntity<User>> getUser(@PathVariable String email) {
        return userService.getUserByEmail(email)
                .map(ResponseEntity::ok);
    }
}

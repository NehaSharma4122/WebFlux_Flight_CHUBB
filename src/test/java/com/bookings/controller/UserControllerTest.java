package com.bookings.controller;

import com.bookings.entity.User;
import com.bookings.exception.*;
import com.bookings.requests.*;
import com.bookings.service.UserService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Mono;

@WebFluxTest(controllers = UserController.class)
class UserControllerTest{

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    UserService userService;

    @Test
    void testRegisterSuccess() {
        User user = new User();
        user.setEmail("neha@example.com");

        UserRequest req = new UserRequest();
        req.setName("Neha");
        req.setEmail("neha@example.com");
        req.setPassword("Neha@1234");

        Mockito.when(userService.registerUser(Mockito.any()))
                .thenReturn(Mono.just(user));

        webTestClient.post()
                .uri("/api/v1.0/flight/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.email").isEqualTo("neha@example.com");
    }

    @Test
    void testLoginWrongPassword() {

        Mockito.when(userService.loginUser(Mockito.any()))
                .thenReturn(Mono.error(new UnauthorizedException("Invalid Password")));

        LoginRequest req = new LoginRequest();
        req.setEmail("neha@example.com");
        req.setPassword("wrong");

        webTestClient.post()
                .uri("/api/v1.0/flight/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void testGetUserByEmail() {

        User user = new User();
        user.setEmail("neha@example.com");

        Mockito.when(userService.getUserByEmail("neha@example.com"))
                .thenReturn(Mono.just(user));

        webTestClient.get()
                .uri("/api/v1.0/flight/user/neha@example.com")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.email").isEqualTo("neha@example.com");
    }
}

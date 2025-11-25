package com.bookings.service;

import com.bookings.entity.*;
import com.bookings.exception.*;
import com.bookings.repository.UserRepository;
import com.bookings.requests.*;

import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId("U001");
        user.setEmail("neha@example.com");
        user.setPassword("Neha@123");
    }

    @Test
    void testRegisterUser() {
        UserRequest request = new UserRequest();
        request.setName("Neha");
        request.setEmail("neha@example.com");
        request.setPassword("Neha@123");

        when(userRepository.existsByEmail("neha@example.com")).thenReturn(Mono.just(false));
        when(userRepository.save(any())).thenReturn(Mono.just(user));

        StepVerifier.create(userService.registerUser(request))
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    void testRegisterUserAlreadyExists() {
        UserRequest req = new UserRequest();
        req.setEmail("neha@example.com");

        when(userRepository.existsByEmail(anyString())).thenReturn(Mono.just(true));

        StepVerifier.create(userService.registerUser(req))
                .expectError(ResourceConflictException.class)
                .verify();
    }

    @Test
    void testLoginSuccess() {
        LoginRequest login = new LoginRequest();
        login.setEmail("neha@example.com");
        login.setPassword("Neha@123");

        when(userRepository.findByEmail(anyString())).thenReturn(Mono.just(user));

        StepVerifier.create(userService.loginUser(login))
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    void testLoginWrongPassword() {
        LoginRequest login = new LoginRequest();
        login.setEmail("neha@example.com");
        login.setPassword("wrong");

        when(userRepository.findByEmail(anyString())).thenReturn(Mono.just(user));

        StepVerifier.create(userService.loginUser(login))
                .expectError(UnauthorizedException.class)
                .verify();
    }

    @Test
    void testLoginEmailNotFound() {
        LoginRequest login = new LoginRequest();
        login.setEmail("x@example.com");

        when(userRepository.findByEmail(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(userService.loginUser(login))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }
}

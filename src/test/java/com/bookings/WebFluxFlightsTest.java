package com.bookings;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

import com.bookings.WebFluxFlightApplication;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class WebFluxFlightsTest {

    @Test
    void mainMethod_runsSuccessfully_withoutStartingServer() {

        try (MockedStatic<SpringApplication> mocked = Mockito.mockStatic(SpringApplication.class)) {

            mocked.when(() ->
                    SpringApplication.run(WebFluxFlightApplication.class, new String[]{})
            ).thenReturn(null);

            assertDoesNotThrow(() ->
                    WebFluxFlightApplication.main(new String[]{})
            );
        }
    }
}
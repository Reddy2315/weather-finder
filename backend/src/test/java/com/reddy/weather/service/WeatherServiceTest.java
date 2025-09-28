package com.reddy.weather.service;

import com.reddy.weather.dto.ForecastResponse;
import com.reddy.weather.dto.ForecastSlot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WeatherServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private WeatherService weatherService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        weatherService = new WeatherService(webClient);
    }

    @Test
    void testGetForecastWithCity_Success() {
        String city = "Hyderabad";
        LocalDate today = LocalDate.now();

        Map<String, Object> main = Map.of("temp", 30.5, "feels_like", 32.0, "humidity", 60);
        Map<String, Object> wind = Map.of("speed", 5.0);
        Map<String, Object> weatherObj = Map.of("main", "Clear", "description", "clear sky");
        Map<String, Object> forecastSlot = new HashMap<>();
        forecastSlot.put("dt_txt", today + " 12:00:00");
        forecastSlot.put("main", main);
        forecastSlot.put("wind", wind);
        forecastSlot.put("weather", Collections.singletonList(weatherObj));

        Map<String, Object> response = Map.of("list", Collections.singletonList(forecastSlot));

        // ✅ Correct mocking for uri lambda
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class))).thenReturn(Mono.just(response));

        StepVerifier.create(weatherService.getForecastWithCity(city, today))
                .assertNext(r -> {
                    assertEquals(city, r.getCity());
                    assertEquals(1, r.getForecast().size());

                    ForecastSlot slot = r.getForecast().get(0);
                    assertEquals("Clear", slot.getWeather().get(0).getMain());
                    assertEquals("clear sky", slot.getWeather().get(0).getDescription());
                    assertEquals(30.5, slot.getMain().getTemp());
                    assertEquals(32.0, slot.getMain().getFeels_like());
                    assertEquals(60, slot.getMain().getHumidity());
                    assertEquals(5.0, slot.getWind().getSpeed());
                })
                .verifyComplete();
    }

    @Test
    void testGetForecastWithCity_EmptyList() {
        String city = "Hyderabad";
        LocalDate today = LocalDate.now();

        Map<String, Object> response = Map.of("list", Collections.emptyList());

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class))).thenReturn(Mono.just(response));

        StepVerifier.create(weatherService.getForecastWithCity(city, today))
                .assertNext(r -> {
                    assertEquals(city, r.getCity());
                    assertTrue(r.getForecast().isEmpty());
                })
                .verifyComplete();
    }

    @Test
    void testGetForecastWithCity_OnError() {
        String city = "Hyderabad";
        LocalDate today = LocalDate.now();

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.error(new RuntimeException("API error")));

        StepVerifier.create(weatherService.getForecastWithCity(city, today))
                .assertNext(r -> {
                    assertEquals(city, r.getCity());
                    assertTrue(r.getForecast().isEmpty());
                })
                .verifyComplete();
    }

    @Test
    void testGetTodayForecast() {
        String city = "Hyderabad";
        LocalDate today = LocalDate.now();

        // Mock getForecastWithCity call
        ForecastResponse mockResponse = new ForecastResponse(city, Collections.emptyList());
        WeatherService spyService = Mockito.spy(weatherService);
        doReturn(Mono.just(mockResponse))
                .when(spyService).getForecastWithCity(city, today);

        StepVerifier.create(spyService.getTodayForecast(city))
                .assertNext(r -> {
                    assertEquals(city, r.getCity());
                    assertTrue(r.getForecast().isEmpty());
                })
                .verifyComplete();
    }

    @Test
    void testGetTomorrowForecast() {
        String city = "Hyderabad";
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        // Mock getForecastWithCity call
        ForecastResponse mockResponse = new ForecastResponse(city, Collections.emptyList());
        WeatherService spyService = Mockito.spy(weatherService);
        doReturn(Mono.just(mockResponse))
                .when(spyService).getForecastWithCity(city, tomorrow);

        StepVerifier.create(spyService.getTomorrowForecast(city))
                .assertNext(r -> {
                    assertEquals(city, r.getCity());
                    assertTrue(r.getForecast().isEmpty());
                })
                .verifyComplete();
    }

}

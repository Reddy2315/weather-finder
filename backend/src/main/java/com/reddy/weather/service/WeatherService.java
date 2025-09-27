package com.reddy.weather.service;

import com.reddy.weather.dto.OpenWeatherResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class WeatherService {

    private final WebClient webClient;

    @Value("${openweather.api.key}")
    private String apiKey;

    public WeatherService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<OpenWeatherResponse> getWeatherByCity(String city) {
        if (apiKey == null || apiKey.isBlank()) {
            return Mono.error(new IllegalStateException("OpenWeather API key is not configured"));
        }

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/data/2.5/weather")
                        .queryParam("q", city)
                        .queryParam("appid", apiKey)
                        .queryParam("units", "metric")
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> Mono.error(new RuntimeException("City not found or bad request")))
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> Mono.error(new RuntimeException("OpenWeather returned server error")))
                .bodyToMono(OpenWeatherResponse.class)
                .map(resp -> resp);
    }

}
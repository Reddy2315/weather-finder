package com.reddy.weather.controller;

import com.reddy.weather.dto.ForecastResponse;
import com.reddy.weather.service.WeatherService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


@CrossOrigin(origins = "http://localhost:4200")
@RestController
@Validated
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/today")
    public Mono<ResponseEntity<ForecastResponse>> getTodayForecast(@RequestParam @NotBlank String city) {
        return weatherService.getTodayForecast(city)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @GetMapping("/tomorrow")
    public Mono<ResponseEntity<ForecastResponse>> getTomorrowForecast(@RequestParam String city) {
        return weatherService.getTomorrowForecast(city)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.internalServerError().build()));
    }

}

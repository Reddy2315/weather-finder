package com.reddy.weather.service;

import com.reddy.weather.dto.ForecastResponse;
import com.reddy.weather.dto.ForecastSlot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
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

    public Mono<ForecastResponse> getForecastWithCity(String city, LocalDate date) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/data/2.5/forecast")
                        .queryParam("q", city)
                        .queryParam("appid", apiKey)
                        .queryParam("units", "metric")
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(response -> {
                    Object rawList = response.get("list");
                    if (!(rawList instanceof List)) {
                        return new ForecastResponse(city, Collections.emptyList());
                    }

                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> list = (List<Map<String, Object>>) rawList;

                    List<ForecastSlot> forecastForDate = list.stream()
                            .filter(item -> {
                                String dtTxt = (String) item.get("dt_txt");
                                if (dtTxt == null) return false;
                                LocalDate slotDate = LocalDate.parse(dtTxt.substring(0, 10));
                                return slotDate.equals(date);
                            })
                            .map(item -> {
                                // Map to DTO manually
                                ForecastSlot slot = new ForecastSlot();
                                slot.setDt_txt((String) item.get("dt_txt"));

                                Map<String, Object> mainMap = (Map<String, Object>) item.get("main");
                                if (mainMap != null) {
                                    com.reddy.weather.dto.MainData main = new com.reddy.weather.dto.MainData();
                                    main.setTemp(((Number) mainMap.get("temp")).doubleValue());
                                    main.setFeels_like(((Number) mainMap.get("feels_like")).doubleValue());
                                    main.setHumidity(((Number) mainMap.get("humidity")).intValue());
                                    slot.setMain(main);
                                }

                                Map<String, Object> windMap = (Map<String, Object>) item.get("wind");
                                if (windMap != null) {
                                    com.reddy.weather.dto.WindData wind = new com.reddy.weather.dto.WindData();
                                    wind.setSpeed(((Number) windMap.get("speed")).doubleValue());
                                    slot.setWind(wind);
                                }

                                List<Map<String, Object>> weatherList = (List<Map<String, Object>>) item.get("weather");
                                if (weatherList != null) {
                                    List<com.reddy.weather.dto.WeatherData> weatherDataList =
                                            weatherList.stream().map(w -> {
                                                com.reddy.weather.dto.WeatherData wd = new com.reddy.weather.dto.WeatherData();
                                                wd.setMain((String) w.get("main"));
                                                wd.setDescription((String) w.get("description"));
                                                return wd;
                                            }).collect(Collectors.toList());
                                    slot.setWeather(weatherDataList);
                                }

                                return slot;
                            })
                            .collect(Collectors.toList());

                    return new ForecastResponse(city, forecastForDate);
                })
                .onErrorResume(e -> Mono.just(new ForecastResponse(city, Collections.emptyList())));
    }

    public Mono<ForecastResponse> getTodayForecast(String city) {
        return getForecastWithCity(city, LocalDate.now());
    }

    public Mono<ForecastResponse> getTomorrowForecast(String city) {
        return getForecastWithCity(city, LocalDate.now().plusDays(1));
    }
}

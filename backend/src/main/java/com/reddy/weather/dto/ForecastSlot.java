package com.reddy.weather.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForecastSlot {

    private String dt_txt;
    private MainData main;
    private WindData wind;
    private List<WeatherData> weather;

}

package com.reddy.weather.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MainData {

    private double temp;
    private double feels_like;
    private int humidity;

}

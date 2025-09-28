import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface ForecastSlot {
  dt_txt: string;
  main: { temp: number; feels_like: number; humidity: number };
  weather: { main: string; description: string }[];
  wind: { speed: number };
}

export interface WeatherResponse {
  city: string;
  forecast: ForecastSlot[];
}

@Injectable({ providedIn: 'root' })
export class WeatherService {
  private baseUrl = 'http://localhost:8080/api/weather';

  constructor(private http: HttpClient) {}

  getTodayWeather(city: string): Observable<WeatherResponse> {
    return this.http.get<WeatherResponse>(`${this.baseUrl}/today?city=${encodeURIComponent(city)}`);
  }

  getTomorrowWeather(city: string): Observable<WeatherResponse> {
    return this.http.get<WeatherResponse>(`${this.baseUrl}/tomorrow?city=${encodeURIComponent(city)}`);
  }

  // getWeather(city: string): Observable<WeatherResponse> {
  //   return this.http.get<WeatherResponse>(`${this.baseUrl}/today?city=${encodeURIComponent(city)}`);
  // }

  // getTomorrowWeather(city: string): Observable<any[]> {
  //   return this.http.get<any[]>(`${this.baseUrl}/tomorrow?city=${encodeURIComponent(city)}`);
  // }

  // getCitySuggestions(query: string): Observable<string[]> {
  //   if (query.length < 3) {
  //     return of([]);
  //   }
  //   return this.http.get<string[]>(`${this.baseUrl}/cities?q=${encodeURIComponent(query)}`);
  // }
}



import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';

export interface WeatherResponse {
  name: string;
  main?: { temp: number; humidity: number; feels_like: number };
  weather: { main: string; description: string }[];
  wind?: { speed: number };
}


@Injectable({ providedIn: 'root' })
export class WeatherService {
  private baseUrl = 'http://localhost:8080/api';


  constructor(private http: HttpClient) { }


  getWeather(city: string): Observable<WeatherResponse> {
    return this.http.get<WeatherResponse>(`${this.baseUrl}/weather?city=${encodeURIComponent(city)}`);
  }

  // getCitySuggestions(query: string): Observable<string[]> {
  //   if (query.length < 3) {
  //     return of([]);
  //   }
  //   return this.http.get<string[]>(`${this.baseUrl}/cities?q=${encodeURIComponent(query)}`);
  // }
}



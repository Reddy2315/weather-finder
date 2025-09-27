import { Component } from '@angular/core';
import { WeatherResponse, WeatherService } from '../weather.service';



@Component({
  selector: 'app-weather-component',
  standalone: false,
  templateUrl: './weather.component.html',
  styleUrl: './weather.component.css'
})
export class WeatherComponent {
  city = '';
  loading = false;
  error = '';
  data?: WeatherResponse;

  
  constructor(private ws: WeatherService) { }


  search() {
    if (!this.city?.trim()) return;
    this.loading = true;
    this.error = '';
    this.data = undefined;


    this.ws.getWeather(this.city.trim()).subscribe({
      next: res => {
        this.data = res;
        this.loading = false;
      },
      error: () => {
        this.error = 'Could not fetch weather. Try another city.';
        this.loading = false;
      }
    });
  }

  getWeatherEmoji(condition: string | undefined): string {
    switch (condition?.toLowerCase()) {
      case 'clear': return '☀️'; case 'clouds': return '☁️';
      case 'rain': return '🌧️'; case 'drizzle': return '🌦️';
      case 'thunderstorm': return '⛈️';
      case 'snow': return '❄️';
      case 'mist': return '🌫️';
      default: return '🌡️';
    }
  }

  getWeatherDescriptionWithEmoji(condition: string | undefined, description: string | undefined): string {
    if (!condition || !description) return description || '';
    switch (condition.toLowerCase()) {
      case 'clouds':
        if (description.toLowerCase().includes('scattered')) return '🌤️ ' + description;
        if (description.toLowerCase().includes('overcast')) return '☁️ ' + description;
        return '☁️ ' + description;
      case 'rain': return '🌧️ ' + description;
      case 'drizzle': return '🌦️ ' + description;
      case 'thunderstorm': return '⛈️ ' + description;
      case 'snow': return '❄️ ' + description;
      case 'mist': return '🌫️ ' + description;
      case 'clear': return '☀️ ' + description;
      default: return '🌡️ ' + description;
    }
  }
}

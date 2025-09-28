import { Component, AfterViewInit, ViewChild } from '@angular/core';
import { ForecastSlot, WeatherResponse, WeatherService } from '../weather.service';
import { Chart, ChartOptions, ChartData, registerables, ChartType } from 'chart.js';

Chart.register(...registerables);

@Component({
  selector: 'app-weather-component',
  standalone: false,
  templateUrl: './weather.component.html',
  styleUrls: ['./weather.component.css']
})
export class WeatherComponent {
  city = '';
  loading = false;
  error = '';
  todayData: ForecastSlot[] = [];
  tomorrowData: ForecastSlot[] = [];
  showTomorrowButton = false;
  tomorrowVisible = false;
  displayCity = '';
  isDay: boolean = true; 

  // Chart.js properties
  chartOptions: ChartOptions<'line'> = {
    responsive: true,
    plugins: { legend: { display: true } }
  };
  chartDataToday: ChartData<'line'> = { labels: [], datasets: [] };
  chartDataTomorrow: ChartData<'line'> = { labels: [], datasets: [] };
  chartType: ChartType = 'line';

  constructor(private ws: WeatherService) {}

updateDayNight() {
  const hour = new Date().getHours();
  this.isDay = hour >= 6 && hour < 18; // 6 AM to 6 PM = day
}

ngOnInit() {
  this.updateDayNight();
}
  search() {
    const cityCamel = this.toCamelCase(this.city);
    if (!cityCamel) return;

    this.loading = true;
    this.error = '';
    this.todayData = [];
    this.tomorrowData = [];
    this.showTomorrowButton = false;
    this.tomorrowVisible = false;
    this.displayCity = '';

    this.ws.getTodayWeather(cityCamel).subscribe({
      next: res => {
        if (!res || !res.forecast || res.forecast.length === 0) {
          this.error = 'City not found or no weather data available.';
        } else {
          this.todayData = res.forecast;
          this.displayCity = res.city;

          // Determine day or night based on current time or first forecast
          const hour = new Date(this.todayData[0].dt_txt).getHours();
          this.isDay = hour >= 6 && hour < 18;

          this.showTomorrowButton = true;
        }
        this.loading = false;
      },
      error: () => {
        this.error = 'Could not fetch weather. Try another city.';
        this.loading = false;
      }
    });
    
  }

  loadTomorrowForecast() {
    const cityCamel = this.toCamelCase(this.city);
    if (!cityCamel) return;

    this.loading = true;
    this.error = '';
    this.chartDataTomorrow = { labels: [], datasets: [] };

    this.ws.getTomorrowWeather(cityCamel).subscribe({
      next: res => {
        if (!res || !res.forecast || res.forecast.length === 0) {
          this.error = 'Tomorrow forecast not available.';
        } else {
          this.tomorrowData = res.forecast;
          this.displayCity = res.city;
          this.tomorrowVisible = true;
          this.showTomorrowButton = false;
          this.updateTomorrowChart(res.forecast);
        }
        this.loading = false;
      },
      error: () => {
        this.error = 'Could not fetch tomorrow forecast.';
        this.loading = false;
      }
    });
  }

  hideTomorrowForecast() {
    this.tomorrowData = [];
    this.tomorrowVisible = false;
    this.showTomorrowButton = true;
    this.chartDataTomorrow = { labels: [], datasets: [] };
  }

  private toCamelCase(str: string): string {
    if (!str) return '';
    return str
      .trim()
      .split(' ')
      .filter(word => word.length > 0)
      .map(word => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase())
      .join(' ');
  }

  private updateTodayChart(forecast: ForecastSlot[]) {
    this.chartDataToday.labels = forecast.map(f => new Date(f.dt_txt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }));
    this.chartDataToday.datasets = [
      {
        data: forecast.map(f => f.main.temp),
        label: 'Temperature (°C)',
        borderColor: '#1976d2',
        backgroundColor: 'rgba(25,118,210,0.2)',
        fill: true,
        tension: 0.4
      }
    ];
  }

  private updateTomorrowChart(forecast: ForecastSlot[]) {
    this.chartDataTomorrow.labels = forecast.map(f => new Date(f.dt_txt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }));
    this.chartDataTomorrow.datasets = [
      {
        data: forecast.map(f => f.main.temp),
        label: 'Temperature (°C)',
        borderColor: '#d32f2f',
        backgroundColor: 'rgba(211,47,47,0.2)',
        fill: true,
        tension: 0.4
      }
    ];
  }

  getWeatherEmoji(condition: string | undefined): string {
    switch (condition?.toLowerCase()) {
      case 'clear': return '☀️';
      case 'clouds': return '☁️';
      case 'rain': return '🌧️';
      case 'drizzle': return '🌦️';
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
import { NgModule, provideBrowserGlobalErrorListeners } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { App } from './app';
import { WeatherComponent } from './weather.component/weather.component';
import { HttpClientModule } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatOptionModule } from '@angular/material/core';



@NgModule({
  declarations: [App, WeatherComponent],
  imports: [ BrowserModule, FormsModule, HttpClientModule, MatIconModule, MatProgressSpinnerModule, MatButtonModule, MatInputModule, MatCardModule,MatFormFieldModule, MatAutocompleteModule, MatOptionModule],
  providers: [ provideBrowserGlobalErrorListeners()],
  bootstrap: [App]
})
export class AppModule { }

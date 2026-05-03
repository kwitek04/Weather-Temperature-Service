package com.example.weather.client.weather;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data Transfer Object representing the current weather details
 * of the Open-Meteo API response.
 */
public record CurrentWeather(
        @JsonProperty("temperature_2m") double temperature
) {}
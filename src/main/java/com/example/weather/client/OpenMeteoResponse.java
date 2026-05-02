package com.example.weather.client;

/**
 * Data Transfer Object representing the root structure of the response
 * returned by the Open-Meteo API.
 * It acts as a wrapper for the nested weather details.
 */
public record OpenMeteoResponse(
        CurrentWeather current
) {}
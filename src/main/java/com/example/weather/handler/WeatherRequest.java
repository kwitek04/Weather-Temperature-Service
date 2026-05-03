package com.example.weather.handler;

/**
 * Data Transfer Object representing the request from the user.
 * It contains the city name for which the weather should be fetched.
 */
public record WeatherRequest(String cityName) {
}
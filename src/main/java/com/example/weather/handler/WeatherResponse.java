package com.example.weather.handler;

import com.example.weather.logic.TemperatureCategory;

/**
 * Data Transfer Object representing the final JSON output of the Lambda function.
 */
public record WeatherResponse(
        double temperature,
        TemperatureCategory category
) {}
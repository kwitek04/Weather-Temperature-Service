package com.example.weather.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.weather.client.WeatherApiClient;
import com.example.weather.logic.TemperatureCategory;
import com.example.weather.logic.TemperatureClassifier;

/**
 * Entry point for the AWS Lambda function.
 * Fetches weather data and returns the temperature with its category.
 */
public class WeatherHandler implements RequestHandler<Object, WeatherResponse> {

    private final WeatherApiClient weatherApiClient;
    private final TemperatureClassifier temperatureClassifier;

    // Default constructor required by AWS Lambda
    public WeatherHandler() {
        this.weatherApiClient = new WeatherApiClient();
        this.temperatureClassifier = new TemperatureClassifier();
    }

    // Method which handles incoming Lambda requests
    @Override
    public WeatherResponse handleRequest(Object input, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Starting weather fetch\n");

        try {
            // Hardcoded coordinates for Wroclaw (task 1 requirement)
            double wroclawLat = 51.1079;
            double wroclawLon = 17.0385;
            logger.log("Coordinates set: lat=" + wroclawLat + ", lon=" + wroclawLon + "\n");

            // fetch temperature from API
            double temperature = weatherApiClient.fetchTemperature(wroclawLat, wroclawLon);

            // determine the category of the temperature
            TemperatureCategory category = temperatureClassifier.classify(temperature);

            return new WeatherResponse(temperature, category);

        } catch (Exception e) {
            logger.log("Error: " + e.getMessage() + "\n");
            throw new RuntimeException("Request failed", e);
        }
    }
}
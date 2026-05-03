package com.example.weather.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.weather.client.geocoding.GeocodingApiClient;
import com.example.weather.client.geocoding.GeocodingResult;
import com.example.weather.client.weather.WeatherApiClient;
import com.example.weather.logic.TemperatureCategory;
import com.example.weather.logic.TemperatureClassifier;

/**
 * Entry point for the AWS Lambda function.
 * Fetches weather data and returns the temperature with its category.
 */
public class WeatherHandler implements RequestHandler<WeatherRequest, WeatherResponse> {

    private final GeocodingApiClient geocodingApiClient;
    private final WeatherApiClient weatherApiClient;
    private final TemperatureClassifier temperatureClassifier;

    // Default constructor required by AWS Lambda
    public WeatherHandler() {
        this.geocodingApiClient = new GeocodingApiClient();
        this.weatherApiClient = new WeatherApiClient();
        this.temperatureClassifier = new TemperatureClassifier();
    }

    // Method which handles incoming Lambda requests
    @Override
    public WeatherResponse handleRequest(WeatherRequest input, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Starting weather fetch\n");

        try {
            // Check if input is correct
            if (input == null || input.cityName() == null || input.cityName().trim().isEmpty()) {
                throw new IllegalArgumentException("City name is required in the request body.");
            }
            // delete spaces from the start and the end
            String cityName = input.cityName().trim();

            logger.log("City set to " + cityName + "\n");

            // Fetch coordinates for given city
            GeocodingResult coordinates = geocodingApiClient.getCoordinates(cityName);

            // fetch temperature from API
            double temperature = weatherApiClient.fetchTemperature(coordinates.latitude(), coordinates.longitude());

            // determine the category of the temperature
            TemperatureCategory category = temperatureClassifier.classify(temperature);

            return new WeatherResponse(temperature, category);

        } catch (Exception e) {
            logger.log("Error: " + e.getMessage() + "\n");
            throw new RuntimeException("Request failed", e);
        }
    }
}
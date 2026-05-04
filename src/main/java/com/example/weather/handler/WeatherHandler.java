package com.example.weather.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.weather.client.geocoding.GeocodingApiClient;
import com.example.weather.client.geocoding.GeocodingResult;
import com.example.weather.client.weather.WeatherApiClient;
import com.example.weather.logic.TemperatureCategory;
import com.example.weather.logic.TemperatureClassifier;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * Entry point for the AWS Lambda function.
 * Fetches weather data and returns the temperature with its category.
 */
public class WeatherHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final GeocodingApiClient geocodingApiClient;
    private final WeatherApiClient weatherApiClient;
    private final TemperatureClassifier temperatureClassifier;
    private final ObjectMapper objectMapper;

    // Default constructor required by AWS Lambda
    public WeatherHandler() {
        this.geocodingApiClient = new GeocodingApiClient();
        this.weatherApiClient = new WeatherApiClient();
        this.temperatureClassifier = new TemperatureClassifier();
        this.objectMapper = new ObjectMapper();
    }

    // Method which handles incoming Lambda requests
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Starting weather fetch\n");

        // Set the header
        Map<String, String> headers = Map.of("Content-Type", "application/json");

        try {
            // Retrieve query parameters from the HTTP request
            Map<String, String> queryParams = input.getQueryStringParameters();

            // Check if input is correct
            if (queryParams == null || !queryParams.containsKey("city") || queryParams.get("city").trim().isEmpty()) {
                String errorBody = objectMapper.writeValueAsString(Map.of("error", "City name is required as a query parameter"));
                return new APIGatewayProxyResponseEvent()
                        .withStatusCode(400)
                        .withHeaders(headers)
                        .withBody(errorBody);
            }

            // delete spaces from the start and the end
            String cityName = queryParams.get("city").trim();
            logger.log("City set to " + cityName + "\n");

            // Fetch coordinates for given city
            GeocodingResult coordinates = geocodingApiClient.getCoordinates(cityName);

            // fetch temperature from API
            double temperature = weatherApiClient.fetchTemperature(coordinates.latitude(), coordinates.longitude());

            // determine the category of the temperature
            TemperatureCategory category = temperatureClassifier.classify(temperature);
            WeatherResponse response = new WeatherResponse(temperature, category);
            String jsonResponse = objectMapper.writeValueAsString(response);

            logger.log("Returning weather for " + cityName + "\n");

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withHeaders(headers)
                    .withBody(jsonResponse);

        } catch (Exception e) {
            logger.log("Error: " + e.getMessage() + "\n");

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(500)
                    .withHeaders(headers)
                    .withBody("{\"error\":\"Internal server error\"}");

        }
    }
}
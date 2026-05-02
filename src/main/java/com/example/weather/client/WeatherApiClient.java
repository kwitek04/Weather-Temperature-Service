package com.example.weather.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * HTTP client responsible for fetching weather data from the Open-Meteo API
 * and mapping JSON responses into Java objects using Jackson.
 */
public class WeatherApiClient {

    // Hardcoded URL with Wroclaw coordinates (task 1 requirement)
    private static final String API_URL = "https://api.open-meteo.com/v1/forecast?latitude=51.1079&longitude=17.0385&current=temperature_2m";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    // Constructor initializing HTTP client and JSON mapper
    public WeatherApiClient() {
        this.httpClient = HttpClient.newHttpClient();

        // Configure Jackson to ignore JSON fields that are not defined in DTO
        this.objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    // Function which fetches current temperature
    public double fetchTemperature() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Failed to fetch weather data. HTTP Status: " + response.statusCode());
            }

            OpenMeteoResponse weatherResponse = objectMapper.readValue(response.body(), OpenMeteoResponse.class);

            return weatherResponse.current().temperature();

        } catch (Exception e) {
            throw new RuntimeException("Error communicating with API", e);
        }
    }
}
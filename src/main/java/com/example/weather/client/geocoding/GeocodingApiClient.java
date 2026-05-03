package com.example.weather.client.geocoding;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
 * HTTP client responsible for communication with the external Geocoding API.
 * It translates a city name into geographical coordinates.
 */
public class GeocodingApiClient {

    // Base URL with placeholder for city name
    private static final String GEOCODING_API_URL = "https://geocoding-api.open-meteo.com/v1/search?name=%s&count=1";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    // Constructor initializing HTTP client and JSON mapper
    public GeocodingApiClient() {
        this.httpClient = HttpClient.newHttpClient();

        // Configure Jackson to ignore JSON fields that are not defined in DTO
        this.objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    // Function which fetches coordinates for a given city name
    public GeocodingResult getCoordinates(String cityName) {
        try {
            // Encode the city name to handle spaces in the URL
            String encodedCityName = URLEncoder.encode(cityName, StandardCharsets.UTF_8);

            // Builds the full API URL using the given city name
            String url = String.format(GEOCODING_API_URL, encodedCityName);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Failed to fetch geocoding data. HTTP Status: " + response.statusCode());
            }

            GeocodingResponse geocodingResponse = objectMapper.readValue(response.body(), GeocodingResponse.class);

            // Check if API returned any results for the given city name
            if (geocodingResponse.results() == null || geocodingResponse.results().isEmpty()) {
                throw new RuntimeException("City not found: " + cityName);
            }

            // Return the first result
            return geocodingResponse.results().getFirst();

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch coordinates for city: " + cityName, e);
        }
    }
}
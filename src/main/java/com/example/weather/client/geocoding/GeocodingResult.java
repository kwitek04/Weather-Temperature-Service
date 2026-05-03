package com.example.weather.client.geocoding;

/**
 * Data Transfer Object representing the specific city details
 * of the Geocoding API response.
 */
public record GeocodingResult(
        double latitude,
        double longitude,
        String name
) {}
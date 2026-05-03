package com.example.weather.client.geocoding;

import java.util.List;

/**
 * Data Transfer Object representing the root structure of the response
 * returned by the Geocoding API.
 * It acts as a wrapper for the nested list of city results.
 */
public record GeocodingResponse(
        List<GeocodingResult> results
) {}
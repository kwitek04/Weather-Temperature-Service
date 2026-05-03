# Weather Temperature Service

## Description

This repository contains an AWS Lambda function written in Java. The primary goal of this service is to fetch the current temperature for a specific location using the Open-Meteo API, classify the temperature into a specific category and return the structured result as a JSON response.

## Key design decisions

The application is structured into clear layers to keep the code simple, modular, and easy to maintain:
* **WeatherHandler** – AWS Lambda entry point which orchestrates the flow.
* **WeatherApiClient**, **GeocodingApiClient** – responsible for HTTP communication with the external APIs.
* **TemperatureClassifier** – business logic for temperature classification.
* **WeatherResponse**, **OpenMeteoResponse**, **CurrentWeather**, **GeocodingResponse**, **GeocodingResult** – Java records acting as Data Transfer Objects for application and API responses.
* **TemperatureCategory** – enum representing the specific results of temperature classification.

The `WeatherHandler` only coordinates the flow and does not contain any business logic.

Java `record` classes are used for Data Transfer Objects instead of traditional POJOs. This choice provides built-in immutability and reduces boilerplate code. Additionally, we map only the strict fields we need from the JSON.

The Open-Meteo Weather API requires latitude and longitude to fetch data. Since we want to request weather by city name, Open-Meteo Geocoding API was integrated. The application first resolves the provided city name into exact coordinates, and then fetches the temperature for that specific location.

The solution utilizes Java's built-in `HttpClient` to call the Open-Meteo API. The JSON responses are mapped to Java objects using the Jackson library.

## Unit testing 

The application can be tested without calling the real API by replacing the `WeatherApiClient` with a mock implementation. For example, using a framework like Mockito, it is possible to simulate API responses and return predefined temperature values. This allows testing how the handler behaves without making real HTTP requests.

The `TemperatureClassifier` can be tested separately because it is independent from the API layer. You can simply pass different temperature values, and check if the correct `TemperatureCategory` is returned.

## Tasks implementation

### Task 1:
To meet the initial requirement, the coordinates for Wrocław (latitude: 51.1079, longitude: 17.0385) are temporarily hardcoded in the `WeatherHandler`. At the same time, the `WeatherApiClient` is designed to accept any coordinates, so it can be easily reused when adding support for dynamic input in the next step.

### Task 2:
The application was upgraded to accept user input via a `WeatherRequest` object containing a `cityName`. A new `GeocodingApiClient` was implemented to translate the requested city name into geographical coordinates. The `client` package was restructured into `weather` and `geocoding` subpackages to separate the two APIs. `WeatherHandler` was refactored so that it validates the input, calls the Geocoding API, passes the retrieved coordinates to the Weather API, and finally classifies the temperature before returning the final `WeatherResponse`.
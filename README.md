# Weather Temperature Service

## Description

This repository contains an AWS Lambda function written in Java. The primary goal of this service is to fetch the current temperature for a specific location using the Open-Meteo API, classify the temperature into a specific category and return the structured result as a JSON response.

## Key design decisions

The application is structured into clear layers to keep the code simple, modular, and easy to maintain:
* **WeatherHandler** – AWS Lambda entry point which orchestrates the flow.
* **WeatherApiClient** – responsible for HTTP communication with the external Open-Meteo API.
* **TemperatureClassifier** – business logic for temperature classification.
* **WeatherResponse**, **OpenMeteoResponse**, **CurrentWeather** – Java records acting as Data Transfer Objects for application and API responses.
* **TemperatureCategory** – enum representing the specific results of temperature classification.

The `WeatherHandler` only coordinates the flow and does not contain any business logic.

Java `record` classes are used for Data Transfer Objects instead of traditional POJOs. This choice provides built-in immutability and reduces boilerplate code. Additionally, we map only the strict fields we need from the JSON.

The solution utilizes Java's built-in `HttpClient` to call the Open-Meteo API. The JSON responses are mapped to Java objects using the Jackson library.

## Unit testing 

The application can be tested without calling the real API by replacing the `WeatherApiClient` with a mock implementation. For example, using a framework like Mockito, it is possible to simulate API responses and return predefined temperature values. This allows testing how the handler behaves without making real HTTP requests.

The `TemperatureClassifier` can be tested separately because it is independent from the API layer. You can simply pass different temperature values, and check if the correct `TemperatureCategory` is returned.

## Tasks implementation

### Task 1:
To meet the initial requirement, the coordinates for Wrocław (latitude: 51.1079, longitude: 17.0385) are temporarily hardcoded in the `WeatherHandler`. At the same time, the `WeatherApiClient` is designed to accept any coordinates, so it can be easily reused when adding support for dynamic input in the next step.
# Weather Temperature Service

## Description

This repository contains an AWS Lambda function written in Java. The primary goal of this service is to fetch the current temperature for a specific location using the Open-Meteo API, classify the temperature into a specific category and return the structured result as a JSON response.

The application is structured into clear layers to keep the code simple, modular, and easy to maintain:
* **WeatherHandler** – AWS Lambda entry point which orchestrates the flow and handles incoming API Gateway events.
* **WeatherApiClient**, **GeocodingApiClient** – responsible for HTTP communication with the external APIs.
* **TemperatureClassifier** – business logic for temperature classification.
* **WeatherResponse**, **OpenMeteoResponse**, **CurrentWeather**, **GeocodingResponse**, **GeocodingResult** – Java records acting as Data Transfer Objects for application and API responses.
* **TemperatureCategory** – enum representing the specific results of temperature classification.

## Application flow
1. The user sends an HTTP request with a city name as a query parameter (e.g. `?city=Toronto`).
2. The `WeatherHandler` receives the request and validates the input.
3. The `GeocodingApiClient` retrieves latitude and longitude for the given city.
4. The `WeatherApiClient` uses these coordinates to fetch the current temperature from the Open-Meteo API.
5. The `TemperatureClassifier` assigns a category based on the temperature value.
6. The result is returned as a JSON response containing the temperature and its category.

## Key design decisions

The `WeatherHandler` only coordinates the flow and does not contain any business logic.

Java `record` classes are used for Data Transfer Objects instead of traditional POJOs. This choice provides built-in immutability and reduces boilerplate code. Additionally, we map only the strict fields we need from the JSON.

The Open-Meteo Weather API requires latitude and longitude to fetch data. Since we want to request weather by city name, Open-Meteo Geocoding API was integrated. The application first resolves the provided city name into exact coordinates, and then fetches the temperature for that specific location.

The solution utilizes Java's built-in `HttpClient` to call the Open-Meteo API. The JSON responses are mapped to Java objects using the Jackson library.

## Unit testing 

The application can be tested without calling the real API by replacing the `WeatherApiClient` with a mock implementation. For example, using a framework like Mockito, it is possible to simulate API responses and return predefined temperature values. This allows testing how the handler behaves without making real HTTP requests.

The `TemperatureClassifier` can be tested separately because it is independent from the API layer. You can simply pass different temperature values, and check if the correct `TemperatureCategory` is returned.

Error handling is implemented at the handler level to return HTTP status codes and JSON error responses.

## Tasks implementation

### Task 1:
To meet the initial requirement, the coordinates for Wrocław (latitude: 51.1079, longitude: 17.0385) are temporarily hardcoded in the `WeatherHandler`. At the same time, the `WeatherApiClient` is designed to accept any coordinates, so it can be easily reused when adding support for dynamic input in the next step.

### Task 2:
The application was upgraded to accept user input via a `WeatherRequest` object containing a `cityName`. A new `GeocodingApiClient` was implemented to translate the requested city name into geographical coordinates. The `client` package was restructured into `weather` and `geocoding` subpackages to separate the two APIs. `WeatherHandler` was refactored so that it validates the input, calls the Geocoding API, passes the retrieved coordinates to the Weather API, and finally classifies the temperature before returning the final `WeatherResponse`.

### Task 3:

The Lambda function is available as a public HTTP endpoint using AWS Lambda Function URL.

The `WeatherHandler` was updated to handle HTTP requests by using `APIGatewayProxyRequestEvent` and `APIGatewayProxyResponseEvent`. This makes it possible to read query parameters from the request and return proper HTTP responses with status codes as 200, 400, and 500.

**Publicly accessible URL:**  
https://fx7hnycqqrcs2vvwtafd77w2gq0umqrf.lambda-url.eu-north-1.on.aws/

**GET parameter name:**  
`city`

**Example request:**
https://fx7hnycqqrcs2vvwtafd77w2gq0umqrf.lambda-url.eu-north-1.on.aws/?city=Toronto

**Example response:**
{
"temperature": 9.5,
"category": "COLD"
}

### Task 4: Design Reflection

The current design separates responsibilities into smaller classes, which makes the code easy to read and understand. Each class has a clear role, and the overall flow is straightforward.

However, the solution is not very flexible when it comes to adding new weather providers. The `WeatherHandler` directly creates specific client implementations, which makes it tightly coupled to one provider. It also depends on provider-specific DTOs. Because of this, adding another provider would require modifying the existing code.

If I had more time, I would create an interface to define a common way of fetching weather data. Different providers could then implement this interface.

I would also use dependency injection to pass the selected provider to the handler instead of creating it directly. Additionally, I would introduce internal DTOs that are independent from any specific API.

This would make the application easier to extend. Adding a new provider would only require creating a new implementation, without changing the existing logic.
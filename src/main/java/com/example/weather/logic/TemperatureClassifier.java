package com.example.weather.logic;

public class TemperatureClassifier {

    public TemperatureCategory classify(double temperature) {
        if (temperature < 0) return TemperatureCategory.FREEZING;
        if (temperature <= 10) return TemperatureCategory.COLD;
        if (temperature <= 20) return TemperatureCategory.MILD;
        if (temperature <= 30) return TemperatureCategory.WARM;
        return TemperatureCategory.HOT;
    }
}
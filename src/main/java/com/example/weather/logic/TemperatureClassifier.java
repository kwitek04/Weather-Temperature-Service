package com.example.weather.logic;

public class TemperatureClassifier {

    public TemperatureCategory classify(double temperature) {
        if (temperature < 0) {
            return TemperatureCategory.FREEZING;
        } else if (temperature >= 0 && temperature <= 10) {
            return TemperatureCategory.COLD;
        } else if (temperature > 10 && temperature <= 20) {
            return TemperatureCategory.MILD;
        } else if (temperature > 20 && temperature <= 30) {
            return TemperatureCategory.WARM;
        } else {
            return TemperatureCategory.HOT;
        }
    }
}
package com.example.fujitsu.service;

import com.example.fujitsu.enumtype.City;
import com.example.fujitsu.enumtype.VehicleType;
import com.example.fujitsu.exception.ForbiddenVehicleException;
import com.example.fujitsu.model.WeatherData;
import com.example.fujitsu.repository.WeatherRepository;
import org.springframework.stereotype.Service;

@Service
public class DeliveryFeeService {

    private final WeatherRepository weatherRepository;

    public DeliveryFeeService(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

    public double calculateDeliveryFee(City city, VehicleType vehicleType) {
        double baseFee = getBaseFee(city, vehicleType);
        WeatherData weatherData = getLatestWeatherData(city);

        double airTemperatureExtraFee = calculateAirTemperatureExtraFee(vehicleType, weatherData);
        double windSpeedExtraFee = calculateWindSpeedExtraFee(vehicleType, weatherData);
        double weatherPhenomenonExtraFee = calculateWeatherPhenomenonExtraFee(vehicleType, weatherData);

        return baseFee + airTemperatureExtraFee + windSpeedExtraFee + weatherPhenomenonExtraFee;
    }

    private double getBaseFee(City city, VehicleType vehicleType) {
        return switch (city) {
            case TALLINN -> switch (vehicleType) {
                case CAR -> 4.0;
                case SCOOTER -> 3.5;
                case BIKE -> 3.0;
            };
            case TARTU -> switch (vehicleType) {
                case CAR -> 3.5;
                case SCOOTER -> 3.0;
                case BIKE -> 2.5;
            };
            case PARNU -> switch (vehicleType) {
                case CAR -> 3.0;
                case SCOOTER -> 2.5;
                case BIKE -> 2.0;
            };
        };
    }

    private WeatherData getLatestWeatherData(City city) {
        String stationName = mapCityToStationName(city);

        return weatherRepository.findTopByStationNameOrderByObservationTimeDesc(stationName)
                .orElseThrow(() -> new RuntimeException("No weather data found for city: " + city));
    }

    private String mapCityToStationName(City city) {
        return switch (city) {
            case TALLINN -> "Tallinn–Harku";
            case TARTU ->  "Tartu–Tõravere";
            case PARNU ->  "Pärnu";
        };
    }

    private double calculateAirTemperatureExtraFee(VehicleType vehicleType, WeatherData weatherData) {
        if (vehicleType != VehicleType.SCOOTER && vehicleType != VehicleType.BIKE) {
            return 0.0;
        }

        double airTemperature = weatherData.getAirTemperature();

        if (airTemperature < -10) {
            return 1.0;
        }
        if (airTemperature >= -10 && airTemperature <= 0) {
            return 0.5;
        }
        return 0.0;
    }

    private double calculateWindSpeedExtraFee(VehicleType vehicleType, WeatherData weatherData) {
        if (vehicleType != VehicleType.BIKE) {
            return 0.0;
        }

        double windSpeed = weatherData.getWindSpeed();

        if (windSpeed > 20) {
            throw new ForbiddenVehicleException("Usage of selected vehicle type is forbidden");
        }
        if (windspeed >= 10 && windSpeed <= 20) {
            return 0.5;
        }
        return 0.0;
    }

    private double calculateWeatherPhenomenonExtraFee(VehicleType vehicleType, WeatherData weatherData) {
        if (vehicleType != VehicleType.SCOOTER && vehicleType != VehicleType.BIKE) {
            return 0.0;
        }

        String phenomenon = weatherData.getWeatherPhenomenon();
        if (phenomenon == null) {
            return 0.0;
        }

        String lowerPhenomenon = phenomenon.toLowerCase();

        if (lowerPhenomenon.contains("glaze") ||
                lowerPhenomenon.contains("hail") ||
                lowerPhenomenon.contains("thunder")) {
            throw new ForbiddenVehicleException("Usage of selected vehicle type is forbidden");
        }

        if (lowerPhenomenon.contains("snow") || lowerPhenomenon.contains("sleet")) {
            return 1.0;
        }

        if (lowerPhenomenon.contains("rain")) {
            return 0.5;
        }

        return 0.0;
    }
}

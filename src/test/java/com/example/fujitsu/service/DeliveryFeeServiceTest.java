package com.example.fujitsu.service;

import com.example.fujitsu.enumtype.City;
import com.example.fujitsu.enumtype.VehicleType;
import com.example.fujitsu.exception.ForbiddenVehicleException;
import com.example.fujitsu.model.WeatherData;
import com.example.fujitsu.repository.WeatherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class DeliveryFeeServiceTest {

    private WeatherRepository weatherRepository;
    private DeliveryFeeService deliveryFeeService;

    @BeforeEach
    public void setup() {
        weatherRepository = Mockito.mock(WeatherRepository.class);
        deliveryFeeService = new DeliveryFeeService(weatherRepository);
    }

    @Test
    void shouldCalculateDeliveryFeeForTartuBikeWithSnow() {
        WeatherData weatherData = new WeatherData();
        weatherData.setStationName("Tartu-Tõravere");
        weatherData.setWmoCode("26242");
        weatherData.setAirTemperature(-2.1);
        weatherData.setWindSpeed(4.7);
        weatherData.setWeatherPhenomenon("Light snow shower");
        weatherData.setObservationTime(LocalDateTime.now());

        when(weatherRepository.findTopByStationNameOrderByObservationTimeDesc("Tartu-Tõravere"))
                .thenReturn(Optional.of(weatherData));

        double result = deliveryFeeService.calculateDeliveryFee(City.TARTU, VehicleType.BIKE);

        assertEquals(4.0, result);
    }

    @Test
    void shouldThrowExceptionWhenBikeUsageIsForbiddenDueToWindSpeed() {
        WeatherData weatherData = new WeatherData();
        weatherData.setStationName("Tartu-Tõravere");
        weatherData.setWmoCode("26242");
        weatherData.setAirTemperature(2.0);
        weatherData.setWindSpeed(21.0);
        weatherData.setWeatherPhenomenon("Clear");
        weatherData.setObservationTime(LocalDateTime.now());

        when(weatherRepository.findTopByStationNameOrderByObservationTimeDesc("Tartu-Tõravere"))
                .thenReturn(Optional.of(weatherData));

        assertThrows(
                ForbiddenVehicleException.class,
                () -> deliveryFeeService.calculateDeliveryFee(City.TARTU, VehicleType.BIKE)
        );
    }

    @Test
    void shouldThrowExceptionWhenScooterUsageIsForbiddenDueToPhenomenon() {
        WeatherData weatherData = new WeatherData();
        weatherData.setStationName("Pärnu");
        weatherData.setWmoCode("41803");
        weatherData.setAirTemperature(1.0);
        weatherData.setWindSpeed(3.0);
        weatherData.setWeatherPhenomenon("Hail");
        weatherData.setObservationTime(LocalDateTime.now());

        when(weatherRepository.findTopByStationNameOrderByObservationTimeDesc("Pärnu"))
                .thenReturn(Optional.of(weatherData));

        assertThrows(
                ForbiddenVehicleException.class,
                () -> deliveryFeeService.calculateDeliveryFee(City.PARNU, VehicleType.SCOOTER)
        );
    }
}

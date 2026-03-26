package com.example.fujitsu.repository;

import com.example.fujitsu.model.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WeatherRepository extends JpaRepository<WeatherData, Long> {

    Optional<WeatherData> findTopByStationNameOrderByObservationTimeDesc(String stationName);
}

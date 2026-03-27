package com.example.fujitsu.controller;

import com.example.fujitsu.service.WeatherImportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * REST controller for manually triggering weather data import.
 */
@RestController
public class WeatherImportController {
    private final WeatherImportService weatherImportService;

    public WeatherImportController(WeatherImportService weatherImportService) {
        this.weatherImportService = weatherImportService;
    }

    /**
     * Imports weather data immediately from the external XML source.
     * Endpoint: GET /import-weather
     *
     * @return success message after import
     */
    @GetMapping("/import-weather")
    public String importWeather() {
        RestTemplate restTemplate = new RestTemplate();
        String xml = restTemplate.getForObject(
                "https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php",
                String.class
        );
        if (xml == null || xml.isBlank()) {
            throw new RuntimeException("Failed to fetch weather data from external service");
        }
        weatherImportService.importWeatherData(xml);
        return "Weather data imported successfully";
    }
}

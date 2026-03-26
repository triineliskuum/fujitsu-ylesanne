package com.example.fujitsu.controller;

import com.example.fujitsu.service.WeatherImportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class WeatherImportController {
    private final WeatherImportService weatherImportService;

    public WeatherImportController(WeatherImportService weatherImportService) {
        this.weatherImportService = weatherImportService;
    }

    @GetMapping("/import-weather")
    public String importWeather() {
        RestTemplate restTemplate = new RestTemplate();
        String xml = restTemplate.getForObject(
                "https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php",
                String.class
        );
        weatherImportService.importWeatherData(xml);
        return "Weather data imported successfully";
    }
}

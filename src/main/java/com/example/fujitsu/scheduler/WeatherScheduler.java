package com.example.fujitsu.scheduler;

import com.example.fujitsu.service.WeatherImportService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Scheduler for periodic weather data import.
 */
@Component
public class WeatherScheduler {

    private final WeatherImportService weatherImportService;

    public WeatherScheduler(WeatherImportService weatherImportService) {
        this.weatherImportService = weatherImportService;
    }

    /**
     * Imports weather data automatically based on the cron expression
     * configured in application properties.
     */
    @Scheduled(cron = "${weather.cron}")
    public void importWeatherDataOnSchedule() {
        RestTemplate restTemplate = new RestTemplate();
        String xml = restTemplate.getForObject(
                "https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php",
                String.class
        );

        if (xml != null && !xml.isBlank()) {
            weatherImportService.importWeatherData(xml);
        }
    }
}
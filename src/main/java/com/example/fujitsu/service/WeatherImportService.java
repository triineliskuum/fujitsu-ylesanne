package com.example.fujitsu.service;

import com.example.fujitsu.model.WeatherData;
import com.example.fujitsu.repository.WeatherRepository;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class WeatherImportService {

    private final WeatherRepository weatherRepository;

    public WeatherImportService(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

    public void importWeatherData(String xmlContent) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlContent)));

            Element root = document.getDocumentElement();

            String timestampValue = root.getAttribute("timestamp");
            LocalDateTime observationTime = parseTimestamp(timestampValue);

            NodeList stations = root.getElementsByTagName("station");

            List<String> allowedStations = List.of(
                    "Tallinn–Harku",
                    "Tartu–Tõravere",
                    "Pärnu"
            );

            for (int i = 0; i < stations.getLength(); i++) {
                Node node = stations.item(i);

                if (node.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }

                Element stationElement = (Element) node;

                String stationName = getTagValue(stationElement, "name");
                if (!allowedStations.contains(stationName)) {
                    continue;
                }

                String wmoCode = getTagValue(stationElement, "wmocode");
                Double airTemperature = parseDouble(getTagValue(stationElement, "airtemperature"));
                Double windSpeed = parseDouble(getTagValue(stationElement, "windspeed"));
                String weatherPhenomenon = getTagValue(stationElement, "phenomenon");

                System.out.println("Station: " + stationName);
                System.out.println("WMO: " + wmoCode);
                System.out.println("Air temp: " + airTemperature);
                System.out.println("Wind speed: " + windSpeed);
                System.out.println("Phenomenon: " + weatherPhenomenon);
                System.out.println("Observation time: " + observationTime);
                System.out.println("---------------");

                if (stationName == null || wmoCode == null || airTemperature == null || windSpeed == null || observationTime == null) {
                    System.out.println("Skipping station because of null required field: " + stationName);
                    continue;
                }

                WeatherData weatherData = new WeatherData();
                weatherData.setStationName(stationName);
                weatherData.setWmoCode(wmoCode);
                weatherData.setAirTemperature(airTemperature);
                weatherData.setWindSpeed(windSpeed);
                weatherData.setWeatherPhenomenon(weatherPhenomenon);
                weatherData.setObservationTime(observationTime);

                weatherRepository.save(weatherData);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to import weather data", e);
        }
    }

    private String getTagValue(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() == 0 || nodeList.item(0) == null) {
            return null;
        }
        return nodeList.item(0).getTextContent();
    }

    private Double parseDouble(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return Double.parseDouble(value);
    }

    private LocalDateTime parseTimestamp(String timestampValue) {
        if (timestampValue == null || timestampValue.isBlank()) {
            return LocalDateTime.now();
        }
        long epochSecond = Long.parseLong(timestampValue);
        return java.time.Instant.ofEpochSecond(epochSecond)
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
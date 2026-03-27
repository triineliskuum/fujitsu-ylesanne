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
import java.util.List;

/**
 * Service for parsing weather data from XML and storing it in the database.
 */
@Service
public class WeatherImportService {

    private static final List<String> ALLOWED_STATIONS = List.of(
            "Tallinn-Harku",
            "Tartu-Tõravere",
            "Pärnu"
    );

    private final WeatherRepository weatherRepository;

    public WeatherImportService(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

    /**
     * Parses XML weather data and saves required station observations.
     *
     * @param xmlContent XML response from external weather service
     */
    public void importWeatherData(String xmlContent) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document document = builder.parse(new InputSource(new StringReader(xmlContent)));

            Element root = document.getDocumentElement();

            String timestampValue = root.getAttribute("timestamp");
            LocalDateTime observationTime = parseTimestamp(timestampValue);

            NodeList stations = root.getElementsByTagName("station");

            for (int i = 0; i < stations.getLength(); i++) {
                Node node = stations.item(i);

                if (node.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }

                Element stationElement = (Element) node;

                String stationName = getTagValue(stationElement, "name");
                if (!ALLOWED_STATIONS.contains(stationName)) {
                    continue;
                }

                String wmoCode = getTagValue(stationElement, "wmocode");
                Double airTemperature = parseDouble(getTagValue(stationElement, "airtemperature"));
                Double windSpeed = parseDouble(getTagValue(stationElement, "windspeed"));
                String weatherPhenomenon = getTagValue(stationElement, "phenomenon");

                if (stationName == null
                        || wmoCode == null
                        || airTemperature == null
                        || windSpeed == null
                        || observationTime == null) {
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

    /**
     * Returns text content of the given XML tag.
     *
     * @param parent parent XML element
     * @param tagName tag name to search for
     * @return tag value or null if tag is missing
     */
    private String getTagValue(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() == 0 || nodeList.item(0) == null) {
            return null;
        }
        return nodeList.item(0).getTextContent();
    }

    /**
     * Parses a numeric value from XML.
     *
     * @param value string value from XML
     * @return parsed double value or null if value is missing
     */
    private Double parseDouble(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return Double.parseDouble(value);
    }

    /**
     * Parses UNIX timestamp value from XML into LocalDateTime.
     *
     * @param timestampValue timestamp value from XML root attribute
     * @return parsed observation time or current time if timestamp is missing
     */
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
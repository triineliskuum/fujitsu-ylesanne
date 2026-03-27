# Fujitsu Java Programming Internship Task – Delivery Fee Service

## Overview

This project is a Spring Boot backend service that calculates delivery fees based on city, vehicle type, and real-time weather conditions.

The application periodically imports weather data from an external XML source, stores it in a database, and applies business rules to calculate delivery fees.

The solution is designed with clean architecture principles and focuses on reliability, testability, and maintainability.

---

## Features

- Weather data import from external XML source
- Scheduled automatic data updates (cron)
- H2 in-memory database
- Delivery fee calculation based on:
  - City
  - Vehicle type
  - Air temperature
  - Wind speed
  - Weather phenomenon
- REST API endpoint for fee calculation
- Global exception handling
- Unit and controller tests

---

## Technologies Used

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- H2 Database
- JUnit 5
- MockMvc
- Gradle

## Weather Data Source

Weather data is fetched from:

https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php

Imported stations:

* Tallinn-Harku
* Tartu-Tõravere
* Pärnu

## How to Run

Clone the repository

```bash
git clone <your-repo-url>
cd <fujitsu-task>
```

### 1. Start the application

Run in IntelliJ (Run button)  
or via terminal:

```bash
./gradlew bootRun
```

### 2. Import weather data

Open in browser:

```bash
http://localhost:8080/import-weather
```

### H2 Database

H2 console is available at:

```bash
http://localhost:8080/h2-console
```

Use:

- JDBC URL: jdbc:h2:mem:testdb
- Username: sa
- Password: (leave empty)

### 3. Calculate delivery fee

Example requests:

```bash
http://localhost:8080/delivery-fee?city=TARTU&vehicleType=BIKE
```

```bash
http://localhost:8080/delivery-fee?city=TALLINN&vehicleType=CAR
```

Example Response

```json
{
  "city": "TARTU",
  "vehicleType": "BIKE",
  "deliveryFee": 2.5
}
```

Example error:

```json
{
  "error": "No weather data found for city: TARTU"
}
```

All errors are handled using a global exception handler.

### Scheduler

Scheduler cron is configured in application.properties:

weather.cron=0 15 * * * *

Default behavior:

- runs once every hour
- at minute 15

For testing, the manual import endpoint can be used.

## Business Logic

Delivery fee is calculated using:

- Base fee depending on city
- Extra fee based on air temperature
- Extra fee based on wind speed
- Extra fee based on weather phenomenon
- Forbidden vehicle conditions

The system always uses the latest weather data per station.

## Supported Stations

- Tallinn → Tallinn-Harku
- Tartu → Tartu-Tõravere
- Pärnu → Pärnu

## Supported vehicles

- CAR
- SCOOTER
- BIKE

## Testing

Includes:

- Service tests
- Controller tests (MockMvc)

Run tests:

```bash
./gradlew test
```

## Project Structure

src/main/java/com/example/fujitsu
├── controller
├── dto
├── enumtype
├── exception
├── model
├── repository
├── scheduler
└── service

## Notes

- H2 database is used (in-memory)
- Scheduler keeps data up-to-date
- Clean layered architecture
- Error handling ensures stable API responses

## Possible Improvements

- Add Swagger/OpenAPI documentation
- Add startup import for initial weather loading
- Store city separately in the database to avoid station name mapping issues

## Design Decisions

- Used H2 in-memory database for simplicity and quick setup
- Implemented scheduler for automated weather updates
- Separated business logic into service layer for testability
- Used global exception handler for consistent error responses

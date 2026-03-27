# Fujitsu Java Programming Internship Task

This project is a Spring Boot application built as a solution for the Fujitsu Java Programming Internship Task.

## Overview

The application calculates delivery fees for food couriers based on:

* city
* vehicle type
* latest weather conditions

It also imports weather data from the Estonian Environment Agency XML feed and stores the observation history in an H2 database.

## Technologies Used

* Java 21
* Spring Boot
* Spring Web
* Spring Data JPA
* H2 Database
* Gradle

## Features

* Weather data import from XML API
* Scheduled weather import using configurable cron expression
* H2 database storage for weather observation history
* Delivery fee calculation based on business rules
* REST API for requesting delivery fees
* Global exception handling for invalid input and forbidden vehicle usage

## Weather Data Source

Weather data is fetched from:

https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php

Imported stations:

* Tallinn-Harku
* Tartu-Tõravere
* Pärnu

## How to Run

1. Clone the repository
2. Open the project in IntelliJ IDEA
3. Make sure Java 21 is installed and configured
4. Run the application

The application starts on:

http://localhost:8080

## H2 Database

H2 console is available at:

http://localhost:8080/h2-console

Use:

* JDBC URL: jdbc:h2:mem:testdb
* Username: sa
* Password: (leave empty)

## Scheduler Configuration

Scheduler cron is configured in application.properties:

weather.cron=0 15 * * * *

Default behavior:

* runs once every hour
* at minute 15

For testing, the manual import endpoint can be used.

## Manual Weather Import Endpoint

GET /import-weather

This endpoint triggers immediate weather data import from the external XML service.

Example:

http://localhost:8080/import-weather

## Delivery Fee Endpoint

GET /delivery-fee?city={CITY}&vehicleType={VEHICLE_TYPE}

Supported cities:

* TALLINN
* TARTU
* PARNU

Supported vehicle types:

* CAR
* SCOOTER
* BIKE

Example requests:

http://localhost:8080/delivery-fee?city=TARTU&vehicleType=BIKE
http://localhost:8080/delivery-fee?city=TALLINN&vehicleType=CAR

Example response:

{
"city": "TARTU",
"vehicleType": "BIKE",
"deliveryFee": 2.5
}

Example error response:

{
"error": "Usage of selected vehicle type is forbidden"
}

## Business Logic Summary

The delivery fee consists of:

* regional base fee
* extra fee based on air temperature
* extra fee based on wind speed
* extra fee based on weather phenomenon

The calculation uses the latest weather data for the selected city.

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

* Weather observation history is preserved in the database by inserting new rows on each import.
* The scheduler uses a configurable cron expression.
* During development, the manual /import-weather endpoint was used for faster testing.

## Possible Improvements

* Add Swagger/OpenAPI documentation
* Add unit and integration tests
* Add startup import for initial weather loading
* Store city separately in the database to avoid station name mapping issues

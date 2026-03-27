package com.example.fujitsu.controller;

import com.example.fujitsu.dto.DeliveryFeeResponse;
import com.example.fujitsu.enumtype.City;
import com.example.fujitsu.enumtype.VehicleType;
import com.example.fujitsu.service.DeliveryFeeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for delivery fee requests.
 */
@RestController
public class DeliveryFeeController {

    private final DeliveryFeeService deliveryFeeService;

    public DeliveryFeeController(DeliveryFeeService deliveryFeeService) {
        this.deliveryFeeService = deliveryFeeService;
    }

    /**
     * Returns delivery fee based on city and vehicle type.
     * Endpoint: GET /delivery-fee
     *
     * @param city selected city (TALLINN, TARTU, PARNU)
     * @param vehicleType selected vehicle type (CAR, SCOOTER, BIKE)
     * @return response containing calculated delivery fee
     */
    @GetMapping("/delivery-fee")
    public DeliveryFeeResponse calculateDeliveryFee(
            @RequestParam City city,
            @RequestParam VehicleType vehicleType
    ) {
        double fee = deliveryFeeService.calculateDeliveryFee(city, vehicleType);
        return new DeliveryFeeResponse(city.name(), vehicleType.name(), fee);
    }
}

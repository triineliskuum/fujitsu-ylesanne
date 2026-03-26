package com.example.fujitsu.controller;

import com.example.fujitsu.dto.DeliveryFeeResponse;
import com.example.fujitsu.enumtype.City;
import com.example.fujitsu.enumtype.VehicleType;
import com.example.fujitsu.service.DeliveryFeeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeliveryFeeController {

    private final DeliveryFeeService deliveryFeeService;

    public DeliveryFeeController(DeliveryFeeService deliveryFeeService) {
        this.deliveryFeeService = deliveryFeeService;
    }

    @GetMapping("/delivery-fee")
    public DeliveryFeeResponse getDeliveryFee(
            @RequestParam City city,
            @RequestParam VehicleType vehicleType
    ) {
        double fee = deliveryFeeService.calculateDeliveryFee(city, vehicleType);
        return new DeliveryFeeResponse(city.name(), vehicleType.name(), fee);
    }
}

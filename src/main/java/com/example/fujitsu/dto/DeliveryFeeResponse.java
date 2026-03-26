package com.example.fujitsu.dto;

public class DeliveryFeeResponse {

    private String city;
    private String vehicleType;
    private double deliveryFee;

    public DeliveryFeeResponse(String city, String vehicleType, double deliveryFee) {
        this.city = city;
        this.vehicleType = vehicleType;
        this.deliveryFee = deliveryFee;
    }

    public String getCity() {
        return city;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public double getDeliveryFee() {
        return deliveryFee;
    }
}

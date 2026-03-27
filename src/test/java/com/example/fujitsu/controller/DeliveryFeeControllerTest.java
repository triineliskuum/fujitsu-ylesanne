package com.example.fujitsu.controller;

import com.example.fujitsu.enumtype.City;
import com.example.fujitsu.enumtype.VehicleType;
import com.example.fujitsu.exception.ForbiddenVehicleException;
import com.example.fujitsu.service.DeliveryFeeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DeliveryFeeController.class)
public class DeliveryFeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeliveryFeeService deliveryFeeService;

    @Test
    @DisplayName("Should return delivery fee JSON for valid request")
    void shouldReturnDeliveryFeeForValidRequest() throws Exception {
        when(deliveryFeeService.calculateDeliveryFee(City.TARTU, VehicleType.BIKE))
                .thenReturn(4.0);

        mockMvc.perform(get("/delivery-fee")
                    .param("city", "TARTU")
                    .param("vehicleType", "BIKE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("TARTU"))
                .andExpect(jsonPath("$.vehicleType").value("BIKE"))
                .andExpect(jsonPath("$.deliveryFee").value(4.0));
    }

    @Test
    @DisplayName("Should return bad request for invalid city")
    void shouldReturnBadRequestForInvalidCity() throws Exception {
        mockMvc.perform(get("/delivery-fee")
                    .param("city", "INVALID_CITY")
                    .param("vehicleType", "BIKE"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid city or vehicle type"));
    }

    @Test
    @DisplayName("Should return forbidden vehicle error when service throws exception")
    void shouldReturnBadRequestWhenVehicleUsageIsForbidden() throws Exception {
        when(deliveryFeeService.calculateDeliveryFee(City.TARTU, VehicleType.BIKE))
                .thenThrow(new ForbiddenVehicleException("Usage of selected vehicle type is forbidden"));

        mockMvc.perform(get("/delivery-fee")
                    .param("city", "TARTU")
                    .param("vehicleType", "BIKE"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Usage of selected vehicle type is forbidden"));
    }
}

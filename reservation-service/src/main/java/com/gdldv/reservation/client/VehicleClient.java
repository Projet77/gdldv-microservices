package com.gdldv.reservation.client;

import com.gdldv.reservation.dto.VehicleDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "vehicle-service", url = "${feign.client.config.vehicle-service.url}")
public interface VehicleClient {

    @GetMapping("/vehicle-service/vehicles/{id}")
    VehicleDTO getVehicleById(@PathVariable Long id);

    @GetMapping("/vehicle-service/vehicles/status/{status}")
    VehicleDTO[] getVehiclesByStatus(@PathVariable String status);
}

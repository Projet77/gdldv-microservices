package com.gdldv.reservation.client;

import com.gdldv.reservation.dto.VehicleDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "vehicle-service", url = "${feign.client.config.vehicle-service.url}")
public interface VehicleClient {

    @GetMapping("/vehicle-service/vehicles/{id}")
    VehicleDTO getVehicleById(@PathVariable Long id);

    @GetMapping("/vehicle-service/vehicles/status/{status}")
    VehicleDTO[] getVehiclesByStatus(@PathVariable String status);

    @GetMapping("/vehicle-service/vehicles")
    Page<VehicleDTO> searchVehicles(
        @RequestParam(required = false) String category,
        @RequestParam(required = false) Double minPrice,
        @RequestParam(required = false) Double maxPrice,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size);
}

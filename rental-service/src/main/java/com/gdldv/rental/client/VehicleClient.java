package com.gdldv.rental.client;

import com.gdldv.reservation.dto.VehicleDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

// NOTE: Les endpoints /rent et /return n'existent pas encore dans le vehicle-service.
// Il faudra les ajouter dans le VehicleController pour que ce client fonctionne.
@FeignClient(name = "vehicle-service", path = "/vehicle-service")
public interface VehicleClient {

    @GetMapping("/vehicles/{id}")
    com.gdldv.reservation.dto.VehicleDTO getVehicleById(@PathVariable("id") Long id);

    @PutMapping("/vehicles/{id}/rent")
    void markVehicleAsRented(@PathVariable("id") Long id);

    @PutMapping("/vehicles/{id}/return")
    void markVehicleAsAvailable(@PathVariable("id") Long id);
}

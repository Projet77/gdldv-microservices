package com.gdldv.vehicle.controller;

import com.gdldv.vehicle.entity.Vehicle;
import com.gdldv.vehicle.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
@Tag(name = "Vehicle", description = "API de gestion des véhicules")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    @Operation(summary = "Récupérer tous les véhicules")
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un véhicule par ID")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable Long id) {
        return vehicleService.getVehicleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/license-plate/{licensePlate}")
    @Operation(summary = "Récupérer un véhicule par immatriculation")
    public ResponseEntity<Vehicle> getVehicleByLicensePlate(@PathVariable String licensePlate) {
        return vehicleService.getVehicleByLicensePlate(licensePlate)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Récupérer les véhicules par statut")
    public ResponseEntity<List<Vehicle>> getVehiclesByStatus(@PathVariable String status) {
        return ResponseEntity.ok(vehicleService.getVehiclesByStatus(status));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Récupérer les véhicules par catégorie")
    public ResponseEntity<List<Vehicle>> getVehiclesByCategory(@PathVariable String category) {
        return ResponseEntity.ok(vehicleService.getVehiclesByCategory(category));
    }

    @PostMapping
    @Operation(summary = "Créer un nouveau véhicule")
    public ResponseEntity<Vehicle> createVehicle(@Valid @RequestBody Vehicle vehicle) {
        Vehicle createdVehicle = vehicleService.createVehicle(vehicle);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVehicle);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un véhicule")
    public ResponseEntity<Vehicle> updateVehicle(@PathVariable Long id, @Valid @RequestBody Vehicle vehicle) {
        try {
            Vehicle updatedVehicle = vehicleService.updateVehicle(id, vehicle);
            return ResponseEntity.ok(updatedVehicle);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un véhicule")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/rent")
    @Operation(summary = "Marquer un véhicule comme loué")
    public ResponseEntity<Void> markVehicleAsRented(@PathVariable Long id) {
        vehicleService.markVehicleAsRented(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/return")
    @Operation(summary = "Marquer un véhicule comme disponible")
    public ResponseEntity<Void> markVehicleAsAvailable(@PathVariable Long id) {
        vehicleService.markVehicleAsAvailable(id);
        return ResponseEntity.ok().build();
    }
}

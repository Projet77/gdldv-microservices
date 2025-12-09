package com.gdldv.vehicle.service;

import com.gdldv.vehicle.dto.CreateVehicleRequest;
import com.gdldv.vehicle.dto.UpdateVehicleRequest;
import com.gdldv.vehicle.dto.VehicleResponse;
import com.gdldv.vehicle.entity.Vehicle;
import com.gdldv.vehicle.entity.VehicleStatus;
import com.gdldv.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    /**
     * Create a new vehicle
     */
    @Transactional
    public VehicleResponse createVehicle(CreateVehicleRequest request) {
        log.info("Creating new vehicle with license plate: {}", request.getLicensePlate());

        // Check if vehicle with same license plate already exists
        vehicleRepository.findByLicensePlateAndIsActiveTrue(request.getLicensePlate())
                .ifPresent(v -> {
                    throw new RuntimeException("Un véhicule avec cette immatriculation existe déjà");
                });

        // Build and save vehicle
        Vehicle vehicle = Vehicle.builder()
                .brand(request.getBrand())
                .model(request.getModel())
                .licensePlate(request.getLicensePlate())
                .color(request.getColor())
                .year(request.getYear())
                .mileage(request.getMileage())
                .dailyPrice(request.getDailyPrice())
                .category(request.getCategory())
                .fuelType(request.getFuelType())
                .transmission(request.getTransmission())
                .description(request.getDescription())
                .status(VehicleStatus.AVAILABLE)
                .isActive(true)
                .build();

        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        log.info("Vehicle created successfully with ID: {}", savedVehicle.getId());

        return mapToResponse(savedVehicle);
    }

    /**
     * Get all active vehicles with pagination
     */
    @Transactional(readOnly = true)
    public Page<VehicleResponse> getAllVehicles(Pageable pageable) {
        log.info("Fetching all active vehicles - Page: {}, Size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return vehicleRepository.findByIsActiveTrue(pageable)
                .map(this::mapToResponse);
    }

    /**
     * Get vehicle by ID
     */
    @Transactional(readOnly = true)
    public VehicleResponse getVehicleById(Long id) {
        log.info("Fetching vehicle with ID: {}", id);
        Vehicle vehicle = vehicleRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new RuntimeException("Véhicule non trouvé avec l'ID: " + id));
        return mapToResponse(vehicle);
    }

    /**
     * Search vehicles with multiple filters
     */
    @Transactional(readOnly = true)
    public Page<VehicleResponse> searchVehicles(String brand, String model, String category,
                                                 VehicleStatus status, Double minPrice,
                                                 Double maxPrice, Pageable pageable) {
        log.info("Searching vehicles with filters - brand: {}, model: {}, category: {}, status: {}, minPrice: {}, maxPrice: {}",
                brand, model, category, status, minPrice, maxPrice);

        return vehicleRepository.searchVehicles(brand, model, category, status, minPrice, maxPrice, pageable)
                .map(this::mapToResponse);
    }

    /**
     * Get vehicles by status
     */
    @Transactional(readOnly = true)
    public Page<VehicleResponse> getVehiclesByStatus(VehicleStatus status, Pageable pageable) {
        log.info("Fetching vehicles with status: {}", status);
        return vehicleRepository.findByStatusAndIsActiveTrue(status, pageable)
                .map(this::mapToResponse);
    }

    /**
     * Get vehicles by category
     */
    @Transactional(readOnly = true)
    public Page<VehicleResponse> getVehiclesByCategory(String category, Pageable pageable) {
        log.info("Fetching vehicles with category: {}", category);
        return vehicleRepository.findByCategoryAndIsActiveTrue(category, pageable)
                .map(this::mapToResponse);
    }

    /**
     * Update vehicle
     */
    @Transactional
    public VehicleResponse updateVehicle(Long id, UpdateVehicleRequest request) {
        log.info("Updating vehicle with ID: {}", id);

        Vehicle vehicle = vehicleRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new RuntimeException("Véhicule non trouvé avec l'ID: " + id));

        // Check if license plate is being changed and if it already exists
        if (request.getLicensePlate() != null && !request.getLicensePlate().equals(vehicle.getLicensePlate())) {
            vehicleRepository.findByLicensePlateAndIsActiveTrue(request.getLicensePlate())
                    .ifPresent(v -> {
                        throw new RuntimeException("Un véhicule avec cette immatriculation existe déjà");
                    });
        }

        // Update fields if provided
        if (request.getBrand() != null) {
            vehicle.setBrand(request.getBrand());
        }
        if (request.getModel() != null) {
            vehicle.setModel(request.getModel());
        }
        if (request.getLicensePlate() != null) {
            vehicle.setLicensePlate(request.getLicensePlate());
        }
        if (request.getColor() != null) {
            vehicle.setColor(request.getColor());
        }
        if (request.getYear() != null) {
            vehicle.setYear(request.getYear());
        }
        if (request.getMileage() != null) {
            vehicle.setMileage(request.getMileage());
        }
        if (request.getDailyPrice() != null) {
            vehicle.setDailyPrice(request.getDailyPrice());
        }
        if (request.getCategory() != null) {
            vehicle.setCategory(request.getCategory());
        }
        if (request.getFuelType() != null) {
            vehicle.setFuelType(request.getFuelType());
        }
        if (request.getTransmission() != null) {
            vehicle.setTransmission(request.getTransmission());
        }
        if (request.getDescription() != null) {
            vehicle.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            vehicle.setStatus(request.getStatus());
        }

        Vehicle updatedVehicle = vehicleRepository.save(vehicle);
        log.info("Vehicle updated successfully with ID: {}", updatedVehicle.getId());

        return mapToResponse(updatedVehicle);
    }

    /**
     * Deactivate vehicle (soft delete)
     */
    @Transactional
    public void deactivateVehicle(Long id) {
        log.info("Deactivating vehicle with ID: {}", id);

        Vehicle vehicle = vehicleRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new RuntimeException("Véhicule non trouvé avec l'ID: " + id));

        vehicle.setIsActive(false);
        vehicleRepository.save(vehicle);

        log.info("Vehicle deactivated successfully with ID: {}", id);
    }

    /**
     * Map Vehicle entity to VehicleResponse DTO
     */
    private VehicleResponse mapToResponse(Vehicle vehicle) {
        return VehicleResponse.builder()
                .id(vehicle.getId())
                .brand(vehicle.getBrand())
                .model(vehicle.getModel())
                .licensePlate(vehicle.getLicensePlate())
                .color(vehicle.getColor())
                .year(vehicle.getYear())
                .mileage(vehicle.getMileage())
                .dailyPrice(vehicle.getDailyPrice())
                .category(vehicle.getCategory())
                .fuelType(vehicle.getFuelType())
                .transmission(vehicle.getTransmission())
                .description(vehicle.getDescription())
                .status(vehicle.getStatus())
                .isActive(vehicle.getIsActive())
                .createdAt(vehicle.getCreatedAt())
                .updatedAt(vehicle.getUpdatedAt())
                .build();
    }
}

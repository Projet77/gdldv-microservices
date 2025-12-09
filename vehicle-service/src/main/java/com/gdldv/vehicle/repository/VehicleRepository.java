package com.gdldv.vehicle.repository;

import com.gdldv.vehicle.entity.Vehicle;
import com.gdldv.vehicle.entity.VehicleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    // Find active vehicle by license plate
    Optional<Vehicle> findByLicensePlateAndIsActiveTrue(String licensePlate);

    // Find active vehicle by ID
    Optional<Vehicle> findByIdAndIsActiveTrue(Long id);

    // Get all active vehicles with pagination
    Page<Vehicle> findByIsActiveTrue(Pageable pageable);

    // Filter by status (active vehicles only)
    Page<Vehicle> findByStatusAndIsActiveTrue(VehicleStatus status, Pageable pageable);

    // Filter by category (active vehicles only)
    Page<Vehicle> findByCategoryAndIsActiveTrue(String category, Pageable pageable);

    // Search by brand (case-insensitive, active vehicles only)
    Page<Vehicle> findByBrandContainingIgnoreCaseAndIsActiveTrue(String brand, Pageable pageable);

    // Search by model (case-insensitive, active vehicles only)
    Page<Vehicle> findByModelContainingIgnoreCaseAndIsActiveTrue(String model, Pageable pageable);

    // Combined search with multiple filters
    @Query("SELECT v FROM Vehicle v WHERE v.isActive = true " +
           "AND (:brand IS NULL OR LOWER(v.brand) LIKE LOWER(CONCAT('%', :brand, '%'))) " +
           "AND (:model IS NULL OR LOWER(v.model) LIKE LOWER(CONCAT('%', :model, '%'))) " +
           "AND (:category IS NULL OR v.category = :category) " +
           "AND (:status IS NULL OR v.status = :status) " +
           "AND (:minPrice IS NULL OR v.dailyPrice >= :minPrice) " +
           "AND (:maxPrice IS NULL OR v.dailyPrice <= :maxPrice)")
    Page<Vehicle> searchVehicles(@Param("brand") String brand,
                                  @Param("model") String model,
                                  @Param("category") String category,
                                  @Param("status") VehicleStatus status,
                                  @Param("minPrice") Double minPrice,
                                  @Param("maxPrice") Double maxPrice,
                                  Pageable pageable);
}

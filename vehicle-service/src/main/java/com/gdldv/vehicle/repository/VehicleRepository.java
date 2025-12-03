package com.gdldv.vehicle.repository;

import com.gdldv.vehicle.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByLicensePlate(String licensePlate);

    List<Vehicle> findByStatus(String status);

    List<Vehicle> findByCategory(String category);

    List<Vehicle> findByBrandAndModel(String brand, String model);
}

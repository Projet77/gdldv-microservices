package com.gdldv.rental.repository;

import com.gdldv.rental.entity.VehicleInspection;
import com.gdldv.rental.entity.InspectionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleInspectionRepository extends JpaRepository<VehicleInspection, Long> {

    /**
     * Trouver toutes les inspections d'une location
     */
    List<VehicleInspection> findByRentalIdOrderByInspectionDateDesc(Long rentalId);

    /**
     * Trouver l'inspection de check-out ou check-in d'une location
     */
    Optional<VehicleInspection> findByRentalIdAndType(Long rentalId, InspectionType type);

    /**
     * Trouver les inspections d'un employé
     */
    List<VehicleInspection> findByInspectedByOrderByInspectionDateDesc(Long employeeId);
}

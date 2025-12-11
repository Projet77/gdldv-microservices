package com.gdldv.user.repository;

import com.gdldv.user.entity.VehicleInspection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface InspectionRepository extends JpaRepository<VehicleInspection, Long> {
    Optional<VehicleInspection> findByCheckOutId(Long checkOutId);
    List<VehicleInspection> findAllByCheckOutId(Long checkOutId);
}

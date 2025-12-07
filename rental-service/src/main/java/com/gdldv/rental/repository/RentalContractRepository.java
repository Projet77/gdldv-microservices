package com.gdldv.rental.repository;

import com.gdldv.rental.entity.RentalContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RentalContractRepository extends JpaRepository<RentalContract, Long> {

    /**
     * Trouver un contrat par numéro de contrat
     */
    Optional<RentalContract> findByContractNumber(String contractNumber);

    /**
     * Trouver un contrat par ID de location
     */
    Optional<RentalContract> findByRentalId(Long rentalId);

    /**
     * Vérifier si un contrat existe pour une location
     */
    boolean existsByRentalId(Long rentalId);
}

package com.gdldv.reservation.service;

import com.gdldv.reservation.client.VehicleClient;
import com.gdldv.reservation.dto.SearchRequest;
import com.gdldv.reservation.dto.VehicleDTO;
import com.gdldv.reservation.entity.Reservation;
import com.gdldv.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

    private final VehicleClient vehicleClient;
    private final ReservationRepository reservationRepository;

    /**
     * Recherche les véhicules disponibles selon les critères
     */
    public Page<VehicleDTO> searchAvailableVehicles(SearchRequest request) {
        log.info("Searching vehicles from {} to {}", request.getStartDate(), request.getEndDate());

        // 1. Récupérer tous les véhicules correspondant aux critères (catégorie, prix)
        Page<VehicleDTO> vehiclesPage = vehicleClient.searchVehicles(
            request.getCategory(),
            request.getMinPrice(),
            request.getMaxPrice(),
            request.getPage(),
            request.getSize()
        );

        // 2. Filtrer les véhicules qui ont des réservations conflictuelles
        List<VehicleDTO> availableVehicles = vehiclesPage.getContent().stream()
            .filter(vehicle -> isVehicleAvailable(vehicle.getId(), request.getStartDate(), request.getEndDate()))
            .filter(vehicle -> vehicle.getIsActive() != null && vehicle.getIsActive())
            .filter(vehicle -> "AVAILABLE".equals(vehicle.getStatus()))
            .collect(Collectors.toList());

        log.info("Found {} available vehicles out of {} total",
                availableVehicles.size(), vehiclesPage.getContent().size());

        // 3. Retourner une nouvelle page avec les véhicules filtrés
        return new PageImpl<>(
            availableVehicles,
            vehiclesPage.getPageable(),
            availableVehicles.size()
        );
    }

    /**
     * Vérifie si un véhicule est disponible pour les dates demandées
     */
    private boolean isVehicleAvailable(Long vehicleId,
                                      java.time.LocalDateTime startDate,
                                      java.time.LocalDateTime endDate) {
        List<Reservation> conflicts = reservationRepository.findConflictingReservations(
            vehicleId, startDate, endDate
        );

        boolean available = conflicts.isEmpty();
        if (!available) {
            log.debug("Vehicle {} has {} conflicting reservations", vehicleId, conflicts.size());
        }
        return available;
    }
}

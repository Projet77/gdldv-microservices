package com.gdldv.rental.service;

import com.gdldv.rental.client.ReservationClient;
import com.gdldv.rental.client.UserClient;
import com.gdldv.rental.client.VehicleClient;
import com.gdldv.rental.dto.*;
import com.gdldv.rental.entity.Rental;
import com.gdldv.rental.entity.VehicleInspection;
import com.gdldv.rental.repository.RentalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RentalService {

    private static final Logger logger = LoggerFactory.getLogger(RentalService.class);

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private InspectionService inspectionService;

    @Autowired
    private ContractService contractService;

    @Autowired
    private UserClient userClient;

    @Autowired
    private VehicleClient vehicleClient;

    @Autowired
    private ReservationClient reservationClient;

    /**
     * GDLDV-468: Créer un dossier de location (Check-out)
     */
    @Transactional
    public RentalDTO checkOut(CheckOutRequest request) {
        logger.info("Début du check-out pour la réservation ID: {}", request.getReservationId());

        // 1. Récupérer la réservation
        ReservationDTO reservation = reservationClient.getReservationById(request.getReservationId());
        if (reservation == null) {
            throw new RuntimeException("Réservation non trouvée");
        }

        // 2. Extraire les informations de la réservation
        Long userId = reservation.getUserId();
        Long vehicleId = reservation.getVehicleId();

        // 3. Vérifier que l'utilisateur et le véhicule existent
        ApiResponse<UserDTO> userResponse = userClient.getUserById(userId);
        if (userResponse == null || !userResponse.isSuccess()) {
            throw new RuntimeException("Utilisateur introuvable ou inactif");
        }

        VehicleDTO vehicle = vehicleClient.getVehicleById(vehicleId);
        if (vehicle == null) {
            throw new RuntimeException("Véhicule introuvable");
        }

        // 4. Créer le dossier de location
        Rental rental = new Rental();
        rental.setReservationId(request.getReservationId());
        rental.setUserId(userId);
        rental.setVehicleId(vehicleId);
        rental.setEmployeeId(request.getEmployeeId());
        rental.setStartDate(reservation.getStartDate());
        rental.setEndDate(reservation.getEndDate());
        rental.setActualStartDate(LocalDateTime.now());
        rental.setPickupLocation(reservation.getPickupLocation());
        rental.setReturnLocation(reservation.getReturnLocation());
        rental.setBasePrice(reservation.getTotalPrice());
        rental.setDeposit(request.getDeposit());
        rental.setTotalPrice(reservation.getTotalPrice());
        rental.setStatus(Rental.RentalStatus.CHECKED_OUT);
        rental.setStartKilometers(request.getStartKilometers());
        rental.setStartFuelLevel(request.getStartFuelLevel());
        rental.setCheckoutNotes(request.getCheckoutNotes());

        Rental savedRental = rentalRepository.save(rental);
        logger.info("Dossier de location créé avec succès: ID={}", savedRental.getId());

        // 5. Créer l'inspection du véhicule (GDLDV-479)
        if (request.getInspection() != null) {
            inspectionService.createInspection(savedRental, request.getInspection(),
                    InspectionType.CHECK_OUT, request.getEmployeeId());
        }

        // 6. Générer le contrat (GDLDV-474)
        contractService.generateContract(savedRental);

        // 7. Marquer le véhicule comme loué
        vehicleClient.markVehicleAsRented(vehicleId);

        // 8. Confirmer la réservation
        reservationClient.confirmReservation(request.getReservationId());

        return convertToDTO(savedRental);
    }

    /**
     * GDLDV-485: Clôturer la location (Check-in)
     */
    @Transactional
    public RentalDTO checkIn(CheckInRequest request) {
        logger.info("Début du check-in pour la location ID: {}", request.getRentalId());

        // 1. Récupérer la location
        Rental rental = rentalRepository.findById(request.getRentalId())
                .orElseThrow(() -> new RuntimeException("Location non trouvée"));

        if (!rental.getStatus().equals(Rental.RentalStatus.CHECKED_OUT) &&
                !rental.getStatus().equals(Rental.RentalStatus.ACTIVE)) {
            throw new RuntimeException("Cette location ne peut pas être clôturée");
        }

        // 2. Mettre à jour les informations de retour
        rental.setActualEndDate(LocalDateTime.now());
        rental.setEndKilometers(request.getEndKilometers());
        rental.setEndFuelLevel(request.getEndFuelLevel());
        rental.setCheckinNotes(request.getCheckinNotes());

        // 3. Créer l'inspection de retour (GDLDV-490)
        if (request.getInspection() != null) {
            inspectionService.createInspection(rental, request.getInspection(),
                    InspectionType.CHECK_IN, request.getEmployeeId());
        }

        // 4. Calculer les frais supplémentaires (GDLDV-494)
        AdditionalChargesDTO charges = calculateAdditionalCharges(rental);
        rental.setAdditionalCharges(charges.getTotalCharges());
        rental.setTotalPrice(rental.getBasePrice().add(charges.getTotalCharges()));

        // 5. Clôturer la location
        rental.setStatus(Rental.RentalStatus.CHECKED_IN);
        Rental updatedRental = rentalRepository.save(rental);

        logger.info("Location clôturée avec succès: ID={}, Frais supplémentaires: {}",
                updatedRental.getId(), charges.getTotalCharges());

        // 6. Marquer le véhicule comme disponible
        vehicleClient.markVehicleAsAvailable(rental.getVehicleId());

        // 7. Marquer la réservation comme terminée
        reservationClient.completeReservation(rental.getReservationId());

        return convertToDTO(updatedRental);
    }

    /**
     * GDLDV-494: Calculer les frais supplémentaires
     */
    public AdditionalChargesDTO calculateAdditionalCharges(Rental rental) {
        logger.info("Calcul des frais supplémentaires pour la location ID: {}", rental.getId());

        AdditionalChargesDTO charges = new AdditionalChargesDTO();
        charges.setRentalId(rental.getId());

        // 1. Frais de retard
        if (rental.getActualEndDate().isAfter(rental.getEndDate())) {
            long daysLate = ChronoUnit.DAYS.between(rental.getEndDate(), rental.getActualEndDate());
            if (daysLate < 1) daysLate = 1; // Minimum 1 jour de retard si l'heure est dépassée
            
            long rentalDurationDays = ChronoUnit.DAYS.between(rental.getStartDate(), rental.getEndDate());
            if (rentalDurationDays < 1) rentalDurationDays = 1;

            BigDecimal dailyRate = rental.getBasePrice().divide(BigDecimal.valueOf(rentalDurationDays), 2, BigDecimal.ROUND_HALF_UP);
            
            BigDecimal lateFee = dailyRate
                    .multiply(BigDecimal.valueOf(daysLate))
                    .multiply(BigDecimal.valueOf(1.5)); // 50% de majoration

            charges.addCharge("Retard de " + daysLate + " jour(s)", lateFee, "Retour tardif");
        }

        // 2. Frais de carburant manquant
        if (rental.getStartFuelLevel() != null && rental.getEndFuelLevel() != null) {
            int fuelDifference = rental.getStartFuelLevel().ordinal() - rental.getEndFuelLevel().ordinal();
            if (fuelDifference > 0) {
                BigDecimal fuelFee = BigDecimal.valueOf(fuelDifference * 20); // 20€ par niveau
                charges.addCharge("Carburant manquant", fuelFee,
                        "Différence de " + fuelDifference + " niveau(x)");
            }
        }

        // 3. Frais de kilométrage excédentaire
        if (rental.getEndKilometers() != null && rental.getStartKilometers() != null) {
            int kmDriven = rental.getEndKilometers() - rental.getStartKilometers();
            long rentalDurationDays = ChronoUnit.DAYS.between(rental.getStartDate(), rental.getEndDate());
            if (rentalDurationDays < 1) rentalDurationDays = 1;
            int allowedKm = (int) rentalDurationDays * 200; // 200km/jour
            
            if (kmDriven > allowedKm) {
                int excessKm = kmDriven - allowedKm;
                BigDecimal kmFee = BigDecimal.valueOf(excessKm * 0.30); // 0,30€/km supplémentaire
                charges.addCharge("Kilométrage excédentaire", kmFee,
                        excessKm + " km au-dessus de la limite");
            }
        }

        logger.info("Frais supplémentaires calculés: {} €", charges.getTotalCharges());
        return charges;
    }

    public Rental getRentalEntityById(Long id) {
        return rentalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location non trouvée"));
    }

    /**
     * Récupérer une location par ID
     */
    public RentalDTO getRentalById(Long id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location non trouvée"));
        return convertToDTO(rental);
    }

    /**
     * Récupérer toutes les locations d'un utilisateur
     */
    public List<RentalDTO> getUserRentals(Long userId) {
        return rentalRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les locations en retard
     */
    public List<RentalDTO> getOverdueRentals() {
        return rentalRepository.findOverdueRentals(LocalDateTime.now())
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Méthodes utilitaires
    private RentalDTO convertToDTO(Rental rental) {
        RentalDTO dto = new RentalDTO();
        dto.setId(rental.getId());
        dto.setReservationId(rental.getReservationId());
        dto.setUserId(rental.getUserId());
        dto.setVehicleId(rental.getVehicleId());
        dto.setEmployeeId(rental.getEmployeeId());
        dto.setStartDate(rental.getStartDate());
        dto.setEndDate(rental.getEndDate());
        dto.setActualStartDate(rental.getActualStartDate());
        dto.setActualEndDate(rental.getActualEndDate());
        dto.setPickupLocation(rental.getPickupLocation());
        dto.setReturnLocation(rental.getReturnLocation());
        dto.setBasePrice(rental.getBasePrice());
        dto.setAdditionalCharges(rental.getAdditionalCharges());
        dto.setTotalPrice(rental.getTotalPrice());
        dto.setDeposit(rental.getDeposit());
        dto.setStatus(rental.getStatus().name());
        dto.setStartKilometers(rental.getStartKilometers());
        dto.setEndKilometers(rental.getEndKilometers());
        dto.setStartFuelLevel(rental.getStartFuelLevel() != null ? rental.getStartFuelLevel().name() : null);
        dto.setEndFuelLevel(rental.getEndFuelLevel() != null ? rental.getEndFuelLevel().name() : null);
        dto.setCheckoutNotes(rental.getCheckoutNotes());
        dto.setCheckinNotes(rental.getCheckinNotes());
        dto.setCreatedAt(rental.getCreatedAt());
        dto.setUpdatedAt(rental.getUpdatedAt());
        return dto;
    }
}
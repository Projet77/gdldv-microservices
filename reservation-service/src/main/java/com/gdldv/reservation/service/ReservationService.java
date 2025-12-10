package com.gdldv.reservation.service;

import com.gdldv.reservation.client.VehicleClient;
import com.gdldv.reservation.dto.CreateReservationRequest;
import com.gdldv.reservation.dto.ReservationResponse;
import com.gdldv.reservation.dto.VehicleDTO;
import com.gdldv.reservation.entity.Reservation;
import com.gdldv.reservation.entity.ReservationOption;
import com.gdldv.reservation.entity.ReservationStatus;
import com.gdldv.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final VehicleClient vehicleClient;
    private final Random random = new Random();

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    public List<Reservation> getReservationsByUserId(Long userId) {
        return reservationRepository.findByUserId(userId);
    }

    public List<Reservation> getReservationsByVehicleId(Long vehicleId) {
        return reservationRepository.findByVehicleId(vehicleId);
    }

    public List<Reservation> getReservationsByStatus(ReservationStatus status) {
        return reservationRepository.findByStatus(status);
    }

    public Optional<Reservation> getReservationByConfirmationNumber(String confirmationNumber) {
        return reservationRepository.findByConfirmationNumber(confirmationNumber);
    }

    /**
     * Crée une nouvelle réservation à partir d'une requête
     */
    @Transactional
    public ReservationResponse createReservation(CreateReservationRequest request) {
        log.info("Creating reservation for vehicle {} and user {}", request.getVehicleId(), request.getUserId());

        // 1. Vérifier que le véhicule existe et est disponible
        VehicleDTO vehicle = vehicleClient.getVehicleById(request.getVehicleId());
        if (vehicle == null) {
            throw new RuntimeException("Vehicle not found with id: " + request.getVehicleId());
        }

        // 2. Vérifier qu'il n'y a pas de conflit de dates
        List<Reservation> conflicts = reservationRepository.findConflictingReservations(
            request.getVehicleId(), request.getStartDate(), request.getEndDate()
        );
        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Vehicle is not available for the selected dates");
        }

        // 3. Calculer le prix total (jours × prix journalier + options)
        long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate());
        double basePrice = days * vehicle.getDailyPrice();

        double optionsPrice = 0.0;
        if (request.getOptions() != null && !request.getOptions().isEmpty()) {
            optionsPrice = request.getOptions().stream()
                .mapToDouble(opt -> opt.getOptionPrice() * opt.getQuantity())
                .sum();
        }

        double totalPrice = basePrice + optionsPrice;

        // 4. Générer un numéro de confirmation unique (CONF-XXXXX)
        String confirmationNumber = generateConfirmationNumber();

        // 5. Créer la réservation
        Reservation reservation = Reservation.builder()
            .confirmationNumber(confirmationNumber)
            .vehicleId(request.getVehicleId())
            .userId(request.getUserId())
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .totalPrice(totalPrice)
            .status(ReservationStatus.PENDING)
            .options(request.getOptions())
            .notes(request.getNotes())
            .build();

        Reservation savedReservation = reservationRepository.save(reservation);
        log.info("Reservation created with confirmation number: {}", confirmationNumber);

        // 6. Retourner la réponse avec les infos du véhicule
        return mapToResponse(savedReservation, vehicle);
    }

    /**
     * Génère un numéro de confirmation unique au format CONF-XXXXX
     */
    private String generateConfirmationNumber() {
        String number;
        do {
            int randomNum = 10000 + random.nextInt(90000); // Génère un nombre entre 10000 et 99999
            number = "CONF-" + randomNum;
        } while (reservationRepository.findByConfirmationNumber(number).isPresent());

        return number;
    }

    @Transactional
    public ReservationResponse updateReservation(Long id, Reservation reservationDetails) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + id));

        reservation.setVehicleId(reservationDetails.getVehicleId());
        reservation.setUserId(reservationDetails.getUserId());
        reservation.setStartDate(reservationDetails.getStartDate());
        reservation.setEndDate(reservationDetails.getEndDate());
        reservation.setStatus(reservationDetails.getStatus());
        reservation.setNotes(reservationDetails.getNotes());

        // Recalculer le prix total
        VehicleDTO vehicle = vehicleClient.getVehicleById(reservation.getVehicleId());
        long days = ChronoUnit.DAYS.between(reservation.getStartDate(), reservation.getEndDate());

        double basePrice = days * vehicle.getDailyPrice();
        double optionsPrice = 0.0;
        if (reservation.getOptions() != null) {
            optionsPrice = reservation.getOptions().stream()
                .mapToDouble(opt -> opt.getOptionPrice() * opt.getQuantity())
                .sum();
        }
        reservation.setTotalPrice(basePrice + optionsPrice);

        Reservation savedReservation = reservationRepository.save(reservation);
        return mapToResponse(savedReservation, vehicle);
    }

    @Transactional
    public void confirmReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + id));
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservationRepository.save(reservation);
        log.info("Reservation {} confirmed", id);
    }

    @Transactional
    public void completeReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + id));
        reservation.setStatus(ReservationStatus.COMPLETED);
        reservationRepository.save(reservation);
        log.info("Reservation {} completed", id);
    }

    @Transactional
    public void cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + id));
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
        log.info("Reservation {} cancelled", id);
    }

    @Transactional
    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }

    public boolean isVehicleAvailable(Long vehicleId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Reservation> conflicts = reservationRepository
                .findConflictingReservations(vehicleId, startDate, endDate);
        return conflicts.isEmpty();
    }

    /**
     * Convertit une Reservation en ReservationResponse
     */
    private ReservationResponse mapToResponse(Reservation reservation, VehicleDTO vehicle) {
        return ReservationResponse.builder()
            .id(reservation.getId())
            .confirmationNumber(reservation.getConfirmationNumber())
            .vehicleId(reservation.getVehicleId())
            .userId(reservation.getUserId())
            .startDate(reservation.getStartDate())
            .endDate(reservation.getEndDate())
            .totalPrice(reservation.getTotalPrice())
            .status(reservation.getStatus())
            .options(reservation.getOptions())
            .stripePaymentIntentId(reservation.getStripePaymentIntentId())
            .notes(reservation.getNotes())
            .createdAt(reservation.getCreatedAt())
            .updatedAt(reservation.getUpdatedAt())
            .vehicle(vehicle)
            .build();
    }
}

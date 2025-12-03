package com.gdldv.reservation.service;

import com.gdldv.reservation.client.VehicleClient;
import com.gdldv.reservation.dto.VehicleDTO;
import com.gdldv.reservation.entity.Reservation;
import com.gdldv.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final VehicleClient vehicleClient;

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

    public List<Reservation> getReservationsByStatus(String status) {
        return reservationRepository.findByStatus(status);
    }

    @Transactional
    public Reservation createReservation(Reservation reservation) {
        // Récupérer les informations du véhicule via Feign Client
        VehicleDTO vehicle = vehicleClient.getVehicleById(reservation.getVehicleId());

        // Calculer le prix total
        long days = ChronoUnit.DAYS.between(reservation.getStartDate(), reservation.getEndDate());
        double totalPrice = days * vehicle.getDailyPrice();
        reservation.setTotalPrice(totalPrice);

        // Définir le statut par défaut
        if (reservation.getStatus() == null) {
            reservation.setStatus("PENDING");
        }

        return reservationRepository.save(reservation);
    }

    @Transactional
    public Reservation updateReservation(Long id, Reservation reservationDetails) {
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
        reservation.setTotalPrice(days * vehicle.getDailyPrice());

        return reservationRepository.save(reservation);
    }

    @Transactional
    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }

    public boolean isVehicleAvailable(Long vehicleId, LocalDate startDate, LocalDate endDate) {
        List<Reservation> existingReservations = reservationRepository
                .findByVehicleIdAndStartDateBetween(vehicleId, startDate, endDate);
        return existingReservations.isEmpty();
    }
}

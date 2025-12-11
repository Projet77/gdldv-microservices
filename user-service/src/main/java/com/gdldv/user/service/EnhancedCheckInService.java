package com.gdldv.user.service;

import com.gdldv.user.dto.CheckInReportDTO;
import com.gdldv.user.dto.DamageReport;
import com.gdldv.user.entity.*;
import com.gdldv.user.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * GDLDV-585: Enhanced Check-in Service (Sprint 3)
 * Service avancé pour le check-in avec rapport de dommages détaillé
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EnhancedCheckInService {

    private final CheckInRepository checkInRepository;
    private final CheckOutRepository checkOutRepository;
    private final InspectionRepository inspectionRepository;
    private final ReservationRepository reservationRepository;
    private final ChargesService chargesService;

    @Transactional
    public CheckInReportDTO performAdvancedCheckIn(
            Long reservationId,
            Long vehicleId,
            Long userId,
            Long mileageAtCheckIn,
            String fuelLevelAtCheckIn,
            List<String> photoUrls,
            String conditionDescription,
            DamageReport damageReport) {

        log.info("Performing advanced check-in for reservation: {}", reservationId);

        // Récupérer réservation
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + reservationId));

        // Récupérer check-out initial
        CheckOut checkOut = checkOutRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Check-out not found for reservation: " + reservationId));

        // Créer check-in
        CheckIn checkIn = CheckIn.builder()
                .reservationId(reservationId)
                .vehicleId(vehicleId)
                .userId(userId)
                .mileageAtCheckIn(mileageAtCheckIn)
                .fuelLevelAtCheckIn(fuelLevelAtCheckIn)
                .vehicleConditionDescription(conditionDescription)
                .photoUrls(photoUrls)
                .build();

        CheckIn savedCheckIn = checkInRepository.save(checkIn);

        // Créer rapport d'inspection
        VehicleInspection finalInspection = createInspectionReport(
                savedCheckIn.getId(),
                damageReport,
                photoUrls
        );

        // Calculer les frais
        double additionalCharges = calculateTotalCharges(
                reservation,
                checkOut,
                checkIn,
                damageReport
        );

        // Mettre à jour les frais dans le check-in
        savedCheckIn.setAdditionalCharges(additionalCharges);
        checkInRepository.save(savedCheckIn);

        // Créer rapport final
        CheckInReportDTO report = CheckInReportDTO.builder()
                .checkInId(savedCheckIn.getId())
                .reservationId(reservationId)
                .vehicleId(vehicleId)
                .mileageDriven(mileageAtCheckIn - checkOut.getMileageAtCheckOut())
                .fuelUsed(calculateFuelUsed(checkOut.getFuelLevelAtCheckOut(), fuelLevelAtCheckIn))
                .damageReport(damageReport)
                .additionalCharges(additionalCharges)
                .photos(photoUrls)
                .inspectionGrade(finalInspection.getGrade().toString())
                .checkInTime(LocalDateTime.now())
                .build();

        log.info("Advanced check-in completed: {}", report.getCheckInId());

        return report;
    }

    private VehicleInspection createInspectionReport(
            Long checkInId,
            DamageReport damageReport,
            List<String> photoUrls) {

        VehicleConditionGrade grade = calculateGrade(damageReport);

        VehicleInspection inspection = VehicleInspection.builder()
                .checkOutId(checkInId)
                .paintIssues(damageReport.getPaintDamage())
                .bumperDamage(damageReport.getBumperDamage())
                .windowDamage(damageReport.getWindowDamage())
                .tireDamage(damageReport.getTireDamage())
                .seatDamage(damageReport.getSeatDamage())
                .generalNotes(damageReport.getGeneralNotes())
                .grade(grade)
                .photoUrls(photoUrls)
                .clientConfirmed(true)
                .build();

        return inspectionRepository.save(inspection);
    }

    private VehicleConditionGrade calculateGrade(DamageReport damageReport) {
        int damageCount = 0;
        if (damageReport.hasPaintDamage()) damageCount++;
        if (damageReport.hasBumperDamage()) damageCount++;
        if (damageReport.hasWindowDamage()) damageCount++;
        if (damageReport.hasTireDamage()) damageCount++;
        if (damageReport.hasSeatDamage()) damageCount++;

        if (damageCount == 0) return VehicleConditionGrade.A_PLUS;
        if (damageCount == 1) return VehicleConditionGrade.A;
        if (damageCount == 2) return VehicleConditionGrade.B;
        return VehicleConditionGrade.C;
    }

    private double calculateTotalCharges(
            Reservation reservation,
            CheckOut checkOut,
            CheckIn checkIn,
            DamageReport damageReport) {

        double totalCharges = 0;

        // Frais km
        long kmDriven = checkIn.getMileageAtCheckIn() - checkOut.getMileageAtCheckOut();
        long chargeableKm = Math.max(0, kmDriven - 200); // Premier 200km inclus
        totalCharges += chargeableKm * 100; // 100 FCFA/km

        // Frais essence
        double fuelUsed = calculateFuelUsed(
                checkOut.getFuelLevelAtCheckOut(),
                checkIn.getFuelLevelAtCheckIn()
        );
        totalCharges += fuelUsed * 700; // 700 FCFA/L

        // Frais dommages
        if (damageReport.hasDamage()) {
            if (damageReport.getEstimatedRepairCost() != null) {
                totalCharges += damageReport.getEstimatedRepairCost();
            } else {
                totalCharges += 10000; // Frais minimum dommage
            }
        }

        // Frais retard (si applicable)
        long minutesLate = ChronoUnit.MINUTES.between(
                reservation.getEndDate(),
                LocalDateTime.now()
        );
        if (minutesLate > 0) {
            totalCharges += (minutesLate / 60) * 5000; // 5000 FCFA/heure
        }

        log.info("Total charges calculated: {} FCFA (km={}, fuel={}, damage={}, late={})",
                totalCharges, chargeableKm * 100, fuelUsed * 700,
                damageReport.hasDamage() ? damageReport.getEstimatedRepairCost() : 0,
                minutesLate > 0 ? (minutesLate / 60) * 5000 : 0);

        return totalCharges;
    }

    private double calculateFuelUsed(String initialFuel, String finalFuel) {
        double initial = fuelLevelToLiters(initialFuel);
        double final_ = fuelLevelToLiters(finalFuel);
        return Math.max(0, initial - final_);
    }

    private double fuelLevelToLiters(String level) {
        if (level == null) return 30; // Default

        switch (level) {
            case "Full":
                return 60;
            case "3/4":
                return 45;
            case "1/2":
                return 30;
            case "1/4":
                return 15;
            case "Empty":
                return 0;
            default:
                return 30;
        }
    }

    public CheckInReportDTO getCheckInReport(Long checkInId) {
        CheckIn checkIn = checkInRepository.findById(checkInId)
                .orElseThrow(() -> new IllegalArgumentException("Check-in not found: " + checkInId));

        CheckOut checkOut = checkOutRepository.findByReservationId(checkIn.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("Check-out not found"));

        VehicleInspection inspection = inspectionRepository.findByCheckOutId(checkInId)
                .orElse(null);

        DamageReport damageReport = null;
        if (inspection != null) {
            damageReport = DamageReport.builder()
                    .paintDamage(inspection.getPaintIssues())
                    .bumperDamage(inspection.getBumperDamage())
                    .windowDamage(inspection.getWindowDamage())
                    .tireDamage(inspection.getTireDamage())
                    .seatDamage(inspection.getSeatDamage())
                    .generalNotes(inspection.getGeneralNotes())
                    .build();
        }

        return CheckInReportDTO.builder()
                .checkInId(checkIn.getId())
                .reservationId(checkIn.getReservationId())
                .vehicleId(checkIn.getVehicleId())
                .mileageDriven(checkIn.getMileageAtCheckIn() - checkOut.getMileageAtCheckOut())
                .fuelUsed(calculateFuelUsed(checkOut.getFuelLevelAtCheckOut(), checkIn.getFuelLevelAtCheckIn()))
                .damageReport(damageReport)
                .additionalCharges(checkIn.getAdditionalCharges())
                .photos(checkIn.getPhotoUrls())
                .inspectionGrade(inspection != null ? inspection.getGrade().toString() : "N/A")
                .checkInTime(checkIn.getCreatedAt())
                .build();
    }
}

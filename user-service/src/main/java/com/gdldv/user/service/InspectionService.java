package com.gdldv.user.service;

import com.gdldv.user.entity.VehicleConditionGrade;
import com.gdldv.user.entity.VehicleInspection;
import com.gdldv.user.repository.InspectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InspectionService {

    private final InspectionRepository inspectionRepository;

    @Transactional
    public VehicleInspection recordInspection(
        Long checkOutId,
        Boolean paintIssues,
        Boolean bumperDamage,
        Boolean windowDamage,
        Boolean tireDamage,
        Boolean seatDamage,
        String notes,
        List<String> photoUrls) {

        log.info("Recording inspection for check-out: {}", checkOutId);

        // Calculate grade based on damages
        VehicleConditionGrade grade = calculateGrade(
            paintIssues, bumperDamage, windowDamage, tireDamage, seatDamage);

        VehicleInspection inspection = VehicleInspection.builder()
            .checkOutId(checkOutId)
            .paintIssues(paintIssues)
            .bumperDamage(bumperDamage)
            .windowDamage(windowDamage)
            .tireDamage(tireDamage)
            .seatDamage(seatDamage)
            .generalNotes(notes)
            .grade(grade)
            .photoUrls(photoUrls)
            .clientConfirmed(false)
            .build();

        VehicleInspection saved = inspectionRepository.save(inspection);
        log.info("Inspection recorded: {}", saved.getId());

        return saved;
    }

    @Transactional
    public void compareAndRecordCheckInInspection(
        Long checkInId,
        Long checkOutId,
        Boolean paintIssues,
        Boolean bumperDamage,
        Boolean windowDamage,
        Boolean tireDamage,
        Boolean seatDamage,
        String notes,
        List<String> photoUrls) {

        log.info("Recording check-in inspection and comparing with check-out");

        // Get initial inspection
        VehicleInspection initialInspection = inspectionRepository.findByCheckOutId(checkOutId)
            .orElseThrow(() -> new IllegalArgumentException("Check-out inspection not found"));

        VehicleConditionGrade finalGrade = calculateGrade(
            paintIssues, bumperDamage, windowDamage, tireDamage, seatDamage);

        // Store final inspection
        VehicleInspection finalInspection = VehicleInspection.builder()
            .checkOutId(checkInId)  // Note: using checkInId
            .paintIssues(paintIssues)
            .bumperDamage(bumperDamage)
            .windowDamage(windowDamage)
            .tireDamage(tireDamage)
            .seatDamage(seatDamage)
            .generalNotes(notes)
            .grade(finalGrade)
            .photoUrls(photoUrls)
            .clientConfirmed(true)
            .build();

        inspectionRepository.save(finalInspection);

        // Compare grades
        log.info("Initial grade: {}, Final grade: {}", initialInspection.getGrade(), finalGrade);

        if (finalGrade.ordinal() < initialInspection.getGrade().ordinal()) {
            log.warn("Vehicle condition has deteriorated!");
        }
    }

    private VehicleConditionGrade calculateGrade(
        Boolean paint, Boolean bumper, Boolean window, Boolean tire, Boolean seat) {

        int damageCount = 0;
        if (Boolean.TRUE.equals(paint)) damageCount++;
        if (Boolean.TRUE.equals(bumper)) damageCount++;
        if (Boolean.TRUE.equals(window)) damageCount++;
        if (Boolean.TRUE.equals(tire)) damageCount++;
        if (Boolean.TRUE.equals(seat)) damageCount++;

        if (damageCount == 0) return VehicleConditionGrade.A_PLUS;
        if (damageCount == 1) return VehicleConditionGrade.A;
        if (damageCount == 2) return VehicleConditionGrade.B;
        return VehicleConditionGrade.C;
    }

    public Optional<VehicleInspection> getInspectionByCheckOut(Long checkOutId) {
        return inspectionRepository.findByCheckOutId(checkOutId);
    }
}

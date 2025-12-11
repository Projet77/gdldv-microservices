package com.gdldv.user.controller;

import com.gdldv.user.dto.*;
import com.gdldv.user.entity.CheckIn;
import com.gdldv.user.service.CheckInService;
import com.gdldv.user.service.EnhancedCheckInService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/check-in")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Check-In", description = "API de gestion des check-ins")
public class CheckInController {

    private final CheckInService checkInService;
    private final EnhancedCheckInService enhancedCheckInService;

    @PostMapping
    @Operation(summary = "Effectuer un check-in")
    public ResponseEntity<CheckInResponse> performCheckIn(
        @Valid @RequestBody CheckInRequest request) {

        CheckIn checkIn = checkInService.performCheckIn(
            request.getReservationId(),
            request.getVehicleId(),
            getCurrentUserId(),
            request.getMileage(),
            request.getFuelLevel(),
            request.getVehicleCondition(),
            request.getPhotoUrls(),
            request.getClientSignature()
        );

        return new ResponseEntity<>(
            mapToResponse(checkIn),
            HttpStatus.CREATED
        );
    }

    @GetMapping("/{checkInId}/charges")
    @Operation(summary = "Récupérer les frais supplémentaires")
    public ResponseEntity<ChargesResponse> getCharges(@PathVariable Long checkInId) {
        return checkInService.getCheckIn(checkInId)
            .map(checkIn -> ResponseEntity.ok(
                ChargesResponse.builder()
                    .checkInId(checkIn.getId())
                    .totalCharges(checkIn.getAdditionalCharges())
                    .description("Km excédentaires, essence, dommages éventuels")
                    .build()
            ))
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/reservation/{reservationId}")
    @Operation(summary = "Récupérer un check-in par réservation")
    public ResponseEntity<CheckInResponse> getCheckIn(
        @PathVariable Long reservationId) {

        return checkInService.getCheckInByReservation(reservationId)
            .map(ci -> ResponseEntity.ok(mapToResponse(ci)))
            .orElse(ResponseEntity.notFound().build());
    }

    private CheckInResponse mapToResponse(CheckIn checkIn) {
        return CheckInResponse.builder()
            .id(checkIn.getId())
            .reservationId(checkIn.getReservationId())
            .mileageAtCheckIn(checkIn.getMileageAtCheckIn())
            .fuelLevelAtCheckIn(checkIn.getFuelLevelAtCheckIn())
            .vehicleConditionDescription(checkIn.getVehicleConditionDescription())
            .additionalCharges(checkIn.getAdditionalCharges())
            .photoUrls(checkIn.getPhotoUrls())
            .createdAt(checkIn.getCreatedAt())
            .build();
    }

    /**
     * GDLDV-585: Check-in avancé avec rapport de dommages (Sprint 3)
     */
    @PostMapping("/advanced")
    @Operation(summary = "Effectuer un check-in avancé avec rapport de dommages")
    public ResponseEntity<CheckInReportDTO> performAdvancedCheckIn(
            @Valid @RequestBody AdvancedCheckInRequest request) {

        DamageReport damageReport = DamageReport.builder()
                .paintDamage(request.getPaintDamage())
                .bumperDamage(request.getBumperDamage())
                .windowDamage(request.getWindowDamage())
                .tireDamage(request.getTireDamage())
                .seatDamage(request.getSeatDamage())
                .generalNotes(request.getGeneralNotes())
                .estimatedRepairCost(request.getEstimatedRepairCost())
                .build();

        CheckInReportDTO report = enhancedCheckInService.performAdvancedCheckIn(
                request.getReservationId(),
                request.getVehicleId(),
                getCurrentUserId(),
                request.getMileage(),
                request.getFuelLevel(),
                request.getPhotoUrls(),
                request.getConditionDescription(),
                damageReport
        );

        return new ResponseEntity<>(report, HttpStatus.CREATED);
    }

    /**
     * GDLDV-585: Récupérer le rapport détaillé d'un check-in
     */
    @GetMapping("/{checkInId}/report")
    @Operation(summary = "Récupérer le rapport détaillé d'un check-in")
    public ResponseEntity<CheckInReportDTO> getCheckInReport(@PathVariable Long checkInId) {
        CheckInReportDTO report = enhancedCheckInService.getCheckInReport(checkInId);
        return ResponseEntity.ok(report);
    }

    private Long getCurrentUserId() {
        // Extract from JWT token
        return 1L;
    }
}

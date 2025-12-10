package com.gdldv.user.controller;

import com.gdldv.user.dto.CheckInRequest;
import com.gdldv.user.dto.CheckInResponse;
import com.gdldv.user.dto.ChargesResponse;
import com.gdldv.user.entity.CheckIn;
import com.gdldv.user.service.CheckInService;
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

    private Long getCurrentUserId() {
        // Extract from JWT token
        return 1L;
    }
}

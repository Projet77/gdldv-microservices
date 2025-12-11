package com.gdldv.user.controller;

import com.gdldv.user.dto.CheckOutRequest;
import com.gdldv.user.dto.CheckOutResponse;
import com.gdldv.user.entity.CheckOut;
import com.gdldv.user.service.CheckOutService;
import com.gdldv.user.service.ContractGenerator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/check-out")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Check-Out", description = "API de gestion des check-outs")
public class CheckOutController {

    private final CheckOutService checkOutService;
    private final ContractGenerator contractGenerator;

    @PostMapping
    @Operation(summary = "Effectuer un check-out")
    public ResponseEntity<CheckOutResponse> performCheckOut(
        @Valid @RequestBody CheckOutRequest request) {

        CheckOut checkOut = checkOutService.performCheckOut(
            request.getReservationId(),
            request.getVehicleId(),
            getCurrentUserId(), // From JWT token
            request.getMileage(),
            request.getFuelLevel(),
            request.getVehicleCondition(),
            request.getPhotoUrls(),
            request.getClientSignature()
        );

        return new ResponseEntity<>(
            mapToResponse(checkOut),
            HttpStatus.CREATED
        );
    }

    @GetMapping("/reservation/{reservationId}")
    @Operation(summary = "Récupérer un check-out par réservation")
    public ResponseEntity<CheckOutResponse> getCheckOut(
        @PathVariable Long reservationId) {

        return checkOutService.getCheckOutByReservation(reservationId)
            .map(co -> ResponseEntity.ok(mapToResponse(co)))
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{checkOutId}/contract")
    @Operation(summary = "Télécharger le contrat PDF")
    public ResponseEntity<byte[]> downloadContract(@PathVariable Long checkOutId) {
        byte[] pdfContent = contractGenerator.generateContractPDF(checkOutId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "contrat-" + checkOutId + ".pdf");

        return ResponseEntity.ok()
            .headers(headers)
            .body(pdfContent);
    }

    private CheckOutResponse mapToResponse(CheckOut checkOut) {
        return CheckOutResponse.builder()
            .id(checkOut.getId())
            .reservationId(checkOut.getReservationId())
            .vehicleId(checkOut.getVehicleId())
            .mileageAtCheckOut(checkOut.getMileageAtCheckOut())
            .fuelLevelAtCheckOut(checkOut.getFuelLevelAtCheckOut())
            .vehicleConditionDescription(checkOut.getVehicleConditionDescription())
            .photoUrls(checkOut.getPhotoUrls())
            .createdAt(checkOut.getCreatedAt())
            .build();
    }

    private Long getCurrentUserId() {
        // Extract from JWT token in header
        // TODO: Implement token parsing
        return 1L;
    }
}

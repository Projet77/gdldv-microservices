package com.gdldv.user.controller;

import com.gdldv.user.dto.InspectionRequest;
import com.gdldv.user.entity.VehicleInspection;
import com.gdldv.user.service.InspectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inspections")
@RequiredArgsConstructor

@Tag(name = "Inspections", description = "API de gestion des inspections de véhicules")
public class InspectionController {

    private final InspectionService inspectionService;

    @PostMapping
    @Operation(summary = "Enregistrer une inspection de véhicule")
    public ResponseEntity<VehicleInspection> recordInspection(
            @Valid @RequestBody InspectionRequest request) {

        VehicleInspection inspection = inspectionService.recordInspection(
                request.getCheckOutId(),
                request.getPaintIssues(),
                request.getBumperDamage(),
                request.getWindowDamage(),
                request.getTireDamage(),
                request.getSeatDamage(),
                request.getGeneralNotes(),
                request.getPhotoUrls());

        return new ResponseEntity<>(inspection, HttpStatus.CREATED);
    }

    @GetMapping("/checkout/{checkOutId}")
    @Operation(summary = "Récupérer l'inspection d'un check-out")
    public ResponseEntity<VehicleInspection> getInspectionByCheckOut(
            @PathVariable Long checkOutId) {

        return inspectionService.getInspectionByCheckOut(checkOutId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

package com.gdldv.reservation.controller;

import com.gdldv.reservation.dto.SearchRequest;
import com.gdldv.reservation.dto.VehicleDTO;
import com.gdldv.reservation.service.SearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class SearchController {

    private final SearchService searchService;

    /**
     * POST /api/reservations/search
     * Recherche les véhicules disponibles selon les critères
     */
    @PostMapping("/search")
    public ResponseEntity<Page<VehicleDTO>> searchAvailableVehicles(
            @Valid @RequestBody SearchRequest request) {

        log.info("Received search request: {}", request);

        Page<VehicleDTO> availableVehicles = searchService.searchAvailableVehicles(request);

        return ResponseEntity.ok(availableVehicles);
    }
}

package com.gdldv.rental.service;

import com.gdldv.rental.dto.VehicleInspectionDTO;
import com.gdldv.rental.entity.Rental;
import com.gdldv.rental.entity.VehicleInspection;
import com.gdldv.rental.repository.VehicleInspectionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InspectionService {

    private static final Logger logger = LoggerFactory.getLogger(InspectionService.class);

    @Autowired
    private VehicleInspectionRepository inspectionRepository;

    /**
     * GDLDV-479: Créer une inspection de véhicule (check-out)
     * GDLDV-490: Créer une inspection de véhicule (check-in)
     */
    @Transactional
    public VehicleInspectionDTO createInspection(
            Rental rental,
            VehicleInspectionDTO inspectionDTO,
            InspectionType type,
            Long employeeId) {

        logger.info("Création d'une inspection {} pour la location ID: {}", type, rental.getId());

        // Créer l'inspection
        VehicleInspection inspection = new VehicleInspection();
        inspection.setRental(rental);
        inspection.setType(type);
        inspection.setInspectedBy(employeeId);
        inspection.setInspectionDate(LocalDateTime.now());
        inspection.setOverallCondition(inspectionDTO.getOverallCondition());

        // Checklist
        inspection.setExteriorClean(inspectionDTO.getExteriorClean() != null ? inspectionDTO.getExteriorClean() : true);
        inspection.setInteriorClean(inspectionDTO.getInteriorClean() != null ? inspectionDTO.getInteriorClean() : true);
        inspection.setTiresCondition(inspectionDTO.getTiresCondition() != null ? inspectionDTO.getTiresCondition() : true);
        inspection.setLightsWorking(inspectionDTO.getLightsWorking() != null ? inspectionDTO.getLightsWorking() : true);
        inspection.setWipersFunctional(inspectionDTO.getWipersFunctional() != null ? inspectionDTO.getWipersFunctional() : true);
        inspection.setSpareWheelPresent(inspectionDTO.getSpareWheelPresent() != null ? inspectionDTO.getSpareWheelPresent() : true);
        inspection.setDocumentsPresent(inspectionDTO.getDocumentsPresent() != null ? inspectionDTO.getDocumentsPresent() : true);
        inspection.setFirstAidKitPresent(inspectionDTO.getFirstAidKitPresent() != null ? inspectionDTO.getFirstAidKitPresent() : true);
        inspection.setWarningTrianglePresent(inspectionDTO.getWarningTrianglePresent() != null ? inspectionDTO.getWarningTrianglePresent() : true);

        // Dommages
        inspection.setDamagesDescription(inspectionDTO.getDamagesDescription());
        inspection.setPhotoUrls(inspectionDTO.getPhotoUrls());
        inspection.setNotes(inspectionDTO.getNotes());

        VehicleInspection savedInspection = inspectionRepository.save(inspection);
        logger.info("Inspection créée avec succès: ID={}, Type={}", savedInspection.getId(), type);

        return convertToDTO(savedInspection);
    }

    /**
     * Récupérer toutes les inspections d'une location
     */
    public List<VehicleInspectionDTO> getInspectionsByRental(Long rentalId) {
        return inspectionRepository.findByRentalIdOrderByInspectionDateDesc(rentalId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer l'inspection de check-out
     */
    public VehicleInspectionDTO getCheckOutInspection(Long rentalId) {
        return inspectionRepository.findByRentalIdAndType(rentalId, InspectionType.CHECK_OUT)
                .map(this::convertToDTO)
                .orElse(null);
    }

    /**
     * Récupérer l'inspection de check-in
     */
    public VehicleInspectionDTO getCheckInInspection(Long rentalId) {
        return inspectionRepository.findByRentalIdAndType(rentalId, InspectionType.CHECK_IN)
                .map(this::convertToDTO)
                .orElse(null);
    }

    /**
     * Comparer les inspections check-out et check-in pour détecter les dommages
     */
    public List<String> compareInspections(Long rentalId) {
        VehicleInspectionDTO checkOut = getCheckOutInspection(rentalId);
        VehicleInspectionDTO checkIn = getCheckInInspection(rentalId);

        List<String> damages = new java.util.ArrayList<>();

        if (checkOut == null || checkIn == null) {
            return damages;
        }

        // Comparer chaque élément de la checklist
        if (checkOut.getExteriorClean() && !checkIn.getExteriorClean()) {
            damages.add("Extérieur sale");
        }
        if (checkOut.getInteriorClean() && !checkIn.getInteriorClean()) {
            damages.add("Intérieur sale");
        }
        if (checkOut.getTiresCondition() && !checkIn.getTiresCondition()) {
            damages.add("Problème avec les pneus");
        }
        if (checkOut.getLightsWorking() && !checkIn.getLightsWorking()) {
            damages.add("Problème avec les lumières");
        }
        if (checkOut.getWipersFunctional() && !checkIn.getWipersFunctional()) {
            damages.add("Problème avec les essuie-glaces");
        }
        if (checkOut.getSpareWheelPresent() && !checkIn.getSpareWheelPresent()) {
            damages.add("Roue de secours manquante");
        }
        if (checkOut.getDocumentsPresent() && !checkIn.getDocumentsPresent()) {
            damages.add("Documents manquants");
        }
        if (checkOut.getFirstAidKitPresent() && !checkIn.getFirstAidKitPresent()) {
            damages.add("Trousse de secours manquante");
        }
        if (checkOut.getWarningTrianglePresent() && !checkIn.getWarningTrianglePresent()) {
            damages.add("Triangle de signalisation manquant");
        }

        // Ajouter les nouveaux dommages constatés
        if (checkIn.getDamagesDescription() != null && !checkIn.getDamagesDescription().isEmpty()) {
            damages.add("Nouveaux dommages: " + checkIn.getDamagesDescription());
        }

        logger.info("Comparaison des inspections pour la location {}: {} dommage(s) détecté(s)", rentalId, damages.size());

        return damages;
    }

    /**
     * Convertir une inspection en DTO
     */
    private VehicleInspectionDTO convertToDTO(VehicleInspection inspection) {
        VehicleInspectionDTO dto = new VehicleInspectionDTO();
        dto.setId(inspection.getId());
        dto.setInspectionType(inspection.getType());
        dto.setOverallCondition(inspection.getOverallCondition());
        dto.setExteriorClean(inspection.getExteriorClean());
        dto.setInteriorClean(inspection.getInteriorClean());
        dto.setTiresCondition(inspection.getTiresCondition());
        dto.setLightsWorking(inspection.getLightsWorking());
        dto.setWipersFunctional(inspection.getWipersFunctional());
        dto.setSpareWheelPresent(inspection.getSpareWheelPresent());
        dto.setDocumentsPresent(inspection.getDocumentsPresent());
        dto.setFirstAidKitPresent(inspection.getFirstAidKitPresent());
        dto.setWarningTrianglePresent(inspection.getWarningTrianglePresent());
        dto.setDamagesDescription(inspection.getDamagesDescription());
        dto.setPhotoUrls(inspection.getPhotoUrls());
        dto.setNotes(inspection.getNotes());
        dto.setInspectionDate(inspection.getInspectionDate());
        return dto;
    }
}
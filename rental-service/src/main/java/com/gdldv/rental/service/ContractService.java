package com.gdldv.rental.service;

import com.gdldv.rental.client.UserClient;
import com.gdldv.rental.client.VehicleClient;
import com.gdldv.rental.dto.ApiResponse;
import com.gdldv.rental.dto.RentalContractDTO;
import com.gdldv.rental.dto.SignContractRequest;
import com.gdldv.rental.dto.UserDTO;
import com.gdldv.reservation.dto.VehicleDTO;
import com.gdldv.rental.entity.Rental;
import com.gdldv.rental.entity.RentalContract;
import com.gdldv.rental.repository.RentalContractRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ContractService {

    private static final Logger logger = LoggerFactory.getLogger(ContractService.class);

    @Autowired
    private RentalContractRepository contractRepository;

    @Autowired
    private UserClient userClient;

    @Autowired
    private VehicleClient vehicleClient;

    /**
     * GDLDV-474: Générer et créer un contrat de location
     */
    @Transactional
    public RentalContractDTO generateContract(Rental rental) {
        logger.info("Génération du contrat pour la location ID: {}", rental.getId());

        if (contractRepository.existsByRentalId(rental.getId())) {
            throw new RuntimeException("Un contrat existe déjà pour cette location");
        }

        // Récupérer les informations du client
        ApiResponse<UserDTO> userResponse = userClient.getUserById(rental.getUserId());
        UserDTO userData = userResponse.getData();
        if (userData == null) {
            throw new RuntimeException("Impossible de récupérer les données de l'utilisateur.");
        }

        // Récupérer les informations du véhicule
        VehicleDTO vehicleData = vehicleClient.getVehicleById(rental.getVehicleId());
        if (vehicleData == null) {
            throw new RuntimeException("Impossible de récupérer les données du véhicule.");
        }

        // Créer le contrat
        RentalContract contract = new RentalContract();
        contract.setContractNumber(RentalContract.generateContractNumber(rental.getId()));
        contract.setRental(rental);
        contract.setContractContent(generateContractContent(rental, userData, vehicleData));
        contract.setTermsAndConditions(generateTermsAndConditions());
        contract.setIsSigned(false);

        RentalContract savedContract = contractRepository.save(contract);
        logger.info("Contrat généré avec succès: {}", savedContract.getContractNumber());

        return convertToDTO(savedContract, userData, vehicleData);
    }

    /**
     * GDLDV-474: Signer le contrat électroniquement
     */
    @Transactional
    public RentalContractDTO signContract(SignContractRequest request, String ipAddress) {
        logger.info("Signature du contrat ID: {}", request.getContractId());

        RentalContract contract = contractRepository.findById(request.getContractId())
                .orElseThrow(() -> new RuntimeException("Contrat non trouvé"));

        if (contract.getIsSigned()) {
            throw new RuntimeException("Ce contrat est déjà signé");
        }

        if (!request.getAcceptTerms()) {
            throw new RuntimeException("Vous devez accepter les conditions générales");
        }

        contract.setClientSignature(request.getSignature());
        contract.setSignedAt(LocalDateTime.now());
        contract.setSignedByIp(ipAddress);
        contract.setIsSigned(true);

        RentalContract signedContract = contractRepository.save(contract);
        logger.info("Contrat signé avec succès: {}", signedContract.getContractNumber());

        return convertToDTO(signedContract, null, null);
    }

    /**
     * Récupérer un contrat par ID
     */
    public RentalContractDTO getContractById(Long id) {
        RentalContract contract = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contrat non trouvé"));

        ApiResponse<UserDTO> userResponse = userClient.getUserById(contract.getRental().getUserId());
        UserDTO userData = userResponse.getData();

        VehicleDTO vehicleData = vehicleClient.getVehicleById(contract.getRental().getVehicleId());

        return convertToDTO(contract, userData, vehicleData);
    }

    /**
     * Récupérer un contrat par numéro de contrat
     */
    public RentalContractDTO getContractByNumber(String contractNumber) {
        RentalContract contract = contractRepository.findByContractNumber(contractNumber)
                .orElseThrow(() -> new RuntimeException("Contrat non trouvé"));

        ApiResponse<UserDTO> userResponse = userClient.getUserById(contract.getRental().getUserId());
        UserDTO userData = userResponse.getData();

        VehicleDTO vehicleData = vehicleClient.getVehicleById(contract.getRental().getVehicleId());

        return convertToDTO(contract, userData, vehicleData);
    }

    /**
     * Générer le contenu HTML du contrat
     */
    private String generateContractContent(Rental rental, UserDTO userData, VehicleDTO vehicleData) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        StringBuilder content = new StringBuilder();
        content.append("<html><head><meta charset='UTF-8'><title>Contrat de Location</title></head><body>");
        content.append("<h1>CONTRAT DE LOCATION DE VÉHICULE</h1>");
        content.append("<h2>Informations Client</h2>");
        content.append("<p><strong>Nom:</strong> ").append(userData.getUsername()).append("</p>");
        content.append("<p><strong>Email:</strong> ").append(userData.getEmail()).append("</p>");
        
        content.append("<h2>Informations Véhicule</h2>");
        content.append("<p><strong>Véhicule:</strong> ").append(vehicleData.getBrand()).append(" ").append(vehicleData.getModel()).append("</p>");
        content.append("<p><strong>Immatriculation:</strong> ").append(vehicleData.getVin()).append("</p>");

        content.append("<h2>Détails de la Location</h2>");
        content.append("<p><strong>Date de début:</strong> ").append(rental.getStartDate().format(formatter)).append("</p>");
        content.append("<p><strong>Date de fin:</strong> ").append(rental.getEndDate().format(formatter)).append("</p>");
        content.append("<p><strong>Lieu de prise:</strong> ").append(rental.getPickupLocation()).append("</p>");
        content.append("<p><strong>Lieu de retour:</strong> ").append(rental.getReturnLocation()).append("</p>");
        content.append("<p><strong>Prix de base:</strong> ").append(rental.getBasePrice()).append(" €</p>");
        content.append("<p><strong>Caution:</strong> ").append(rental.getDeposit()).append(" €</p>");

        content.append("<h2>Responsabilités du Locataire</h2>");
        content.append("<ul>");
        content.append("<li>Utiliser le véhicule conformément à sa destination</li>");
        content.append("<li>Respecter le Code de la Route</li>");
        content.append("<li>Ne pas sous-louer le véhicule</li>");
        content.append("<li>Restituer le véhicule dans l'état de départ</li>");
        content.append("</ul>");

        content.append("</body></html>");

        return content.toString();
    }

    /**
     * Générer les conditions générales
     */
    private String generateTermsAndConditions() {
        StringBuilder terms = new StringBuilder();
        terms.append("CONDITIONS GÉNÉRALES DE LOCATION\n\n");
        terms.append("1. Le locataire doit être âgé de 21 ans minimum et titulaire d'un permis de conduire valide depuis au moins 2 ans.\n");
        terms.append("2. Le véhicule doit être restitué avec le même niveau de carburant qu'au départ.\n");
        terms.append("3. Tout retard de restitution sera facturé avec une majoration de 50%.\n");
        terms.append("4. Le locataire est responsable de tous les dommages causés au véhicule pendant la période de location.\n");
        terms.append("5. La caution sera restituée dans un délai de 7 jours après la restitution du véhicule sans dommage.\n");
        terms.append("6. Il est interdit de fumer dans le véhicule.\n");
        terms.append("7. Le kilométrage est limité à 200 km par jour. Tout dépassement sera facturé 0,30€/km.\n");
        terms.append("8. Le locataire s'engage à respecter toutes les lois en vigueur.\n");

        return terms.toString();
    }

    /**
     * Convertir un contrat en DTO
     */
    private RentalContractDTO convertToDTO(RentalContract contract, UserDTO userData, VehicleDTO vehicleData) {
        RentalContractDTO dto = new RentalContractDTO();
        dto.setId(contract.getId());
        dto.setContractNumber(contract.getContractNumber());
        dto.setRentalId(contract.getRental().getId());

        if (userData != null) {
            dto.setClientName(userData.getUsername());
            dto.setClientEmail(userData.getEmail());
        }

        if (vehicleData != null) {
            dto.setVehicleBrand(vehicleData.getBrand());
            dto.setVehicleModel(vehicleData.getModel());
            dto.setVehiclePlate(vehicleData.getVin());
        }

        dto.setStartDate(contract.getRental().getStartDate());
        dto.setEndDate(contract.getRental().getEndDate());
        dto.setPickupLocation(contract.getRental().getPickupLocation());
        dto.setReturnLocation(contract.getRental().getReturnLocation());
        dto.setBasePrice(contract.getRental().getBasePrice());
        dto.setDeposit(contract.getRental().getDeposit());
        dto.setTotalPrice(contract.getRental().getTotalPrice());
        dto.setContractContent(contract.getContractContent());
        dto.setTermsAndConditions(contract.getTermsAndConditions());
        dto.setIsSigned(contract.getIsSigned());
        dto.setSignedAt(contract.getSignedAt());
        dto.setPdfUrl(contract.getPdfUrl());
        dto.setCreatedAt(contract.getCreatedAt());

        return dto;
    }
}
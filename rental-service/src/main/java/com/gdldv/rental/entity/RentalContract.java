package com.gdldv.rental.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "rental_contracts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentalContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String contractNumber; // Numéro unique du contrat

    @OneToOne
    @JoinColumn(name = "rental_id", nullable = false, unique = true)
    private Rental rental;

    // Contenu du contrat
    @Column(columnDefinition = "TEXT", nullable = false)
    private String contractContent; // Contenu HTML ou texte du contrat

    @Column(columnDefinition = "TEXT")
    private String termsAndConditions; // Conditions générales

    // Signature électronique
    @Column
    private String clientSignature; // Base64 ou URL de la signature

    @Column
    private LocalDateTime signedAt;

    @Column
    private String signedByIp; // IP de signature

    @Column(nullable = false)
    private Boolean isSigned = false;

    // Document généré
    @Column
    private String pdfUrl; // URL ou chemin du PDF généré

    // Timestamps
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Méthode utilitaire pour générer un numéro de contrat
    public static String generateContractNumber(Long rentalId) {
        return "CONT-" + System.currentTimeMillis() + "-" + rentalId;
    }
}
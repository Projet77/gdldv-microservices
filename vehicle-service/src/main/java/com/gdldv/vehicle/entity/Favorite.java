package com.gdldv.vehicle.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * GDLDV-515: Entité pour la gestion des favoris (wishlist)
 */
@Entity
@Table(name = "favorites", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "vehicle_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "L'ID de l'utilisateur est obligatoire")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull(message = "L'ID du véhicule est obligatoire")
    @Column(name = "vehicle_id", nullable = false)
    private Long vehicleId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

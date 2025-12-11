package com.gdldv.user.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DamageReport {
    private Boolean paintDamage;
    private Boolean bumperDamage;
    private Boolean windowDamage;
    private Boolean tireDamage;
    private Boolean seatDamage;
    private String generalNotes;
    private Double estimatedRepairCost;

    public Boolean hasDamage() {
        return (paintDamage != null && paintDamage) ||
               (bumperDamage != null && bumperDamage) ||
               (windowDamage != null && windowDamage) ||
               (tireDamage != null && tireDamage) ||
               (seatDamage != null && seatDamage);
    }

    public Boolean hasPaintDamage() {
        return paintDamage != null && paintDamage;
    }

    public Boolean hasBumperDamage() {
        return bumperDamage != null && bumperDamage;
    }

    public Boolean hasWindowDamage() {
        return windowDamage != null && windowDamage;
    }

    public Boolean hasTireDamage() {
        return tireDamage != null && tireDamage;
    }

    public Boolean hasSeatDamage() {
        return seatDamage != null && seatDamage;
    }
}

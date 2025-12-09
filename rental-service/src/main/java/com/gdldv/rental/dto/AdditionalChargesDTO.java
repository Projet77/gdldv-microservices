package com.gdldv.rental.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdditionalChargesDTO {

    private Long rentalId;
    private BigDecimal totalCharges = BigDecimal.ZERO;
    private List<ChargeItem> charges = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChargeItem {
        private String description;
        private BigDecimal amount;
        private String reason;
    }

    public void addCharge(String description, BigDecimal amount, String reason) {
        charges.add(new ChargeItem(description, amount, reason));
        totalCharges = totalCharges.add(amount);
    }
}

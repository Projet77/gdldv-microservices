package com.gdldv.reservation.service;

import com.gdldv.reservation.entity.ReservationStatus;
import com.gdldv.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoyaltyService {

    private final ReservationRepository reservationRepository;

    // Default discount percentage (can be updated by Admin)
    private double discountPercentage = 10.0;

    // Threshold for loyalty
    private static final int LOYALTY_THRESHOLD = 5;

    public double getDiscountPercentageForUser(Long userId) {
        long completedReservations = reservationRepository.countByUserIdAndStatus(userId, ReservationStatus.COMPLETED);

        if (completedReservations > LOYALTY_THRESHOLD) {
            log.info("User {} is loyal ({} reservations). Applying {}% discount.", userId, completedReservations,
                    discountPercentage);
            return discountPercentage;
        }

        return 0.0;
    }

    public void setLoyaltyDiscount(double percentage) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100");
        }
        this.discountPercentage = percentage;
        log.info("Loyalty discount updated to {}%", percentage);
    }

    public double getCurrentLoyaltyDiscount() {
        return this.discountPercentage;
    }
}

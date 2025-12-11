package com.gdldv.user.service;

import com.gdldv.user.entity.CheckOut;
import com.gdldv.user.repository.CheckOutRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckOutService {

    private final CheckOutRepository checkOutRepository;

    @Transactional
    public CheckOut performCheckOut(
        Long reservationId,
        Long vehicleId,
        Long userId,
        Long mileage,
        String fuelLevel,
        String vehicleCondition,
        List<String> photoUrls,
        String clientSignature) {

        log.info("Performing check-out for reservation: {}", reservationId);

        CheckOut checkOut = CheckOut.builder()
            .reservationId(reservationId)
            .vehicleId(vehicleId)
            .userId(userId)
            .mileageAtCheckOut(mileage)
            .fuelLevelAtCheckOut(fuelLevel)
            .vehicleConditionDescription(vehicleCondition)
            .photoUrls(photoUrls)
            .clientSignature(clientSignature)
            .build();

        CheckOut saved = checkOutRepository.save(checkOut);
        log.info("Check-out completed: {}", saved.getId());

        return saved;
    }

    public Optional<CheckOut> getCheckOutByReservation(Long reservationId) {
        return checkOutRepository.findByReservationId(reservationId);
    }

    public Optional<CheckOut> getCheckOutById(Long id) {
        return checkOutRepository.findById(id);
    }
}

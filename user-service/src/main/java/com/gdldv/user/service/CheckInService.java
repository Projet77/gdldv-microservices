package com.gdldv.user.service;

import com.gdldv.user.entity.CheckIn;
import com.gdldv.user.entity.CheckOut;
import com.gdldv.user.repository.CheckInRepository;
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
public class CheckInService {

    private final CheckInRepository checkInRepository;
    private final CheckOutRepository checkOutRepository;
    private final ChargesService chargesService;

    @Transactional
    public CheckIn performCheckIn(
        Long reservationId,
        Long vehicleId,
        Long userId,
        Long mileage,
        String fuelLevel,
        String vehicleCondition,
        List<String> photoUrls,
        String clientSignature) {

        log.info("Performing check-in for reservation: {}", reservationId);

        // Get initial state from check-out
        CheckOut checkOut = checkOutRepository.findByReservationId(reservationId)
            .orElseThrow(() -> new IllegalArgumentException("Check-out not found for reservation: " + reservationId));

        // Calculate additional charges
        Double charges = chargesService.calculateCharges(
            checkOut.getMileageAtCheckOut(),
            mileage,
            checkOut.getFuelLevelAtCheckOut(),
            fuelLevel
        );

        CheckIn checkIn = CheckIn.builder()
            .reservationId(reservationId)
            .vehicleId(vehicleId)
            .userId(userId)
            .mileageAtCheckIn(mileage)
            .fuelLevelAtCheckIn(fuelLevel)
            .vehicleConditionDescription(vehicleCondition)
            .photoUrls(photoUrls)
            .clientSignature(clientSignature)
            .additionalCharges(charges)
            .build();

        CheckIn saved = checkInRepository.save(checkIn);
        log.info("Check-in completed: {}", saved.getId());

        return saved;
    }

    public Optional<CheckIn> getCheckIn(Long checkInId) {
        return checkInRepository.findById(checkInId);
    }

    public Optional<CheckIn> getCheckInByReservation(Long reservationId) {
        return checkInRepository.findByReservationId(reservationId);
    }
}

package com.gdldv.rental.client;

import com.gdldv.rental.dto.ReservationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

// NOTE: Les endpoints /confirm et /complete n'existent pas encore dans le reservation-service.
// Il faudra les ajouter dans le ReservationController pour que ce client fonctionne.
@FeignClient(name = "reservation-service", path = "/reservation-service")
public interface ReservationClient {

    @GetMapping("/reservations/{id}")
    ReservationDTO getReservationById(@PathVariable("id") Long id);

    @PutMapping("/reservations/{id}/confirm")
    void confirmReservation(@PathVariable("id") Long id);

    @PutMapping("/reservations/{id}/complete")
    void completeReservation(@PathVariable("id") Long id);
}

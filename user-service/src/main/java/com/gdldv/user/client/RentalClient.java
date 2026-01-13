package com.gdldv.user.client;

import com.gdldv.user.dto.ApiResponse;
import com.gdldv.user.dto.RentalDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "rental-service", path = "/rental-service")
public interface RentalClient {

    @GetMapping("/api/rentals/user/{userId}")
    ApiResponse<List<RentalDTO>> getUserRentals(@PathVariable("userId") Long userId);
}

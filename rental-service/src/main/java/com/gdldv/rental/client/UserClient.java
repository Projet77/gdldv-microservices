package com.gdldv.rental.client;

import com.gdldv.rental.dto.ApiResponse;
import com.gdldv.rental.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// TODO: The target endpoint in user-service requires ADMIN or EMPLOYEE role.
// This will fail unless security is configured to allow inter-service communication.
// A dedicated internal endpoint in user-service would be a better approach.
@FeignClient(name = "user-service", path = "/user-service")
public interface UserClient {

    @GetMapping("/api/users/internal/{id}")
    ApiResponse<UserDTO> getUserById(@PathVariable("id") Long id);
}

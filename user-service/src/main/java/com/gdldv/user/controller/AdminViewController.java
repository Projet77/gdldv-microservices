package com.gdldv.user.controller;

import com.gdldv.user.dto.VehicleDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminViewController {

    @GetMapping("/vehicles/{id}")
    public String getVehicleDetails(@PathVariable Long id, Model model) {
        // This is mock data. In a real application, you would fetch this from the vehicle-service.
        VehicleDTO vehicle = new VehicleDTO(
            id,
            "Toyota",
            "Camry",
            2024,
            "DK-4532-AB",
            "Berline",
            25000,
            "Disponible",
            45320,
            "01/03/2025",
            "OK"
        );

        model.addAttribute("vehicle", vehicle);
        return "admin/vehicle-details";
    }
}

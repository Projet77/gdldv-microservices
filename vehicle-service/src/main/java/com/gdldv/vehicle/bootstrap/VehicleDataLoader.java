package com.gdldv.vehicle.bootstrap;

import com.gdldv.vehicle.entity.Vehicle;
import com.gdldv.vehicle.entity.VehicleStatus;
import com.gdldv.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class VehicleDataLoader implements CommandLineRunner {

    private final VehicleRepository vehicleRepository;

    @Override
    public void run(String... args) throws Exception {
        if (vehicleRepository.count() == 0) {
            log.info("Seeding vehicle database with homepage data...");
            loadVehicles();
            log.info("Vehicle database seeded successfully.");
        } else {
            log.info("Vehicle database already contains data. Skipping seed.");
        }
    }

    private void loadVehicles() {
        List<Vehicle> vehicles = Arrays.asList(
                createVehicle("Lamborghini", "Urus", 2024, "SUV Sport", 250000, "ESSENCE", "AUTOMATIC", 5,
                        "Le SUV le plus rapide du monde. Puissance brute et luxe absolu.",
                        "https://images.unsplash.com/photo-1669644383182-4161ae3d2426?q=80&w=2690&auto=format&fit=crop"), // Urus

                createVehicle("Ferrari", "F8 Tributo", 2023, "Supercar", 350000, "ESSENCE", "AUTOMATIC", 2,
                        "Une icône de Maranello. V8 biturbo, 720 chevaux.",
                        "https://images.unsplash.com/photo-1592198084033-aade902d1aae?q=80&w=2670&auto=format&fit=crop"), // Ferrari

                createVehicle("Mercedes-Benz", "G63 AMG", 2024, "SUV Luxe", 200000, "ESSENCE", "AUTOMATIC", 5,
                        "Le roi des 4x4. Imposant, bruyant, luxueux.",
                        "https://images.unsplash.com/photo-1606220838315-056192d5e927?q=80&w=2574&auto=format&fit=crop"), // G-Wagon

                createVehicle("Porsche", "911 GT3", 2023, "Sport", 280000, "ESSENCE", "AUTOMATIC", 2,
                        "La perfection sur circuit et sur route.",
                        "https://images.unsplash.com/photo-1503376763036-066120622c74?q=80&w=2670&auto=format&fit=crop"), // 911

                createVehicle("Range Rover", "Autobiography", 2024, "SUV Luxe", 180000, "DIESEL", "AUTOMATIC", 5,
                        "Le summum du confort britannique.",
                        "https://images.unsplash.com/photo-1605218427306-633ba87c9759?q=80&w=2670&auto=format&fit=crop"), // Range
                                                                                                                          // Rover

                createVehicle("Audi", "RSQ8", 2023, "SUV Sport", 190000, "ESSENCE", "AUTOMATIC", 5,
                        "La cousine technique de l'Urus, plus discrète mais tout aussi redoutable.",
                        "https://images.unsplash.com/photo-1614200179396-27777009e993?q=80&w=2670&auto=format&fit=crop"), // RSQ8

                createVehicle("Bentley", "Continental GT", 2022, "Grand Tourisme", 300000, "ESSENCE", "AUTOMATIC", 4,
                        "Voyagez en première classe, au volant.",
                        "https://images.unsplash.com/photo-1563720223185-11003d516935?q=80&w=2670&auto=format&fit=crop"), // Bentley

                createVehicle("Rolls Royce", "Cullinan", 2024, "Ultra Luxe", 500000, "ESSENCE", "AUTOMATIC", 4,
                        "Le SUV le plus luxueux jamais construit.",
                        "https://images.unsplash.com/photo-1631295868223-632658512537?q=80&w=2670&auto=format&fit=crop"), // Cullinan

                createVehicle("McLaren", "720S", 2023, "Supercar", 320000, "ESSENCE", "AUTOMATIC", 2,
                        "Une aérodynamique sculptée par le vent.",
                        "https://images.unsplash.com/photo-1621135802920-133df287f89c?q=80&w=2670&auto=format&fit=crop"), // McLaren

                createVehicle("Tesla", "Model X Plaid", 2024, "Électrique", 150000, "ELECTRIC", "AUTOMATIC", 6,
                        "1020 chevaux, électrique, familial. Falcon Wings.",
                        "https://images.unsplash.com/photo-1617788138017-80ad40651399?q=80&w=2670&auto=format&fit=crop"), // Tesla
                                                                                                                          // X

                createVehicle("BMW", "M4 Competition", 2024, "Sport", 160000, "ESSENCE", "AUTOMATIC", 4,
                        "Le coupé sportif par excellence.",
                        "https://images.unsplash.com/photo-1617814066861-1c390cb54f9a?q=80&w=2670&auto=format&fit=crop"), // BMW
                                                                                                                          // M4

                createVehicle("Aston Martin", "DBX 707", 2023, "SUV Sport", 260000, "ESSENCE", "AUTOMATIC", 5,
                        "Le SUV de James Bond. 707 chevaux.",
                        "https://images.unsplash.com/photo-1678809415682-82f53412a84d?q=80&w=2670&auto=format&fit=crop"), // Aston
                                                                                                                          // Martin
                                                                                                                          // DBX

                createVehicle("Peugeot", "208", 2023, "Citadine", 30000, "ESSENCE", "MANUAL", 5,
                        "La citadine préférée des français.",
                        "https://images.unsplash.com/photo-1541899481282-d53bffe3c35d?q=80&w=2670&auto=format&fit=crop"), // Peugeot
                                                                                                                          // (Generic)

                createVehicle("Renault", "Clio V", 2023, "Citadine", 25000, "DIESEL", "MANUAL", 5,
                        "Polyvalente et confortable.",
                        "https://images.unsplash.com/photo-1621007947382-bb3c3994e3fb?q=80&w=2670&auto=format&fit=crop") // Renault
                                                                                                                         // (Generic)
        );

        vehicleRepository.saveAll(vehicles);
    }

    private Vehicle createVehicle(String brand, String model, int year, String category, double price, String fuel,
            String transmission, int seats, String desc, String imageUrl) {
        return Vehicle.builder()
                .brand(brand)
                .model(model)
                .year(year)
                .category(category)
                .dailyPrice(BigDecimal.valueOf(price))
                .licensePlate("SN-" + (int) (Math.random() * 9000 + 1000) + "-AA")
                .status(VehicleStatus.AVAILABLE)
                .mileage((int) (Math.random() * 5000))
                .fuelType(fuel)
                .transmission(transmission)
                .seats(seats)
                .babySeat(true)
                .images(Arrays.asList(imageUrl))
                .description(desc)
                .build();
    }
}

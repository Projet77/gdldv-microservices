package com.gdldv.vehicle.bootstrap;

import com.gdldv.vehicle.entity.Vehicle;
import com.gdldv.vehicle.entity.VehicleStatus;
import com.gdldv.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class VehicleDataLoader implements CommandLineRunner {

        private final VehicleRepository vehicleRepository;

        @Override
        public void run(String... args) throws Exception {
                log.info("Checking vehicle database seed status...");
                loadVehicles();
                log.info("Vehicle database check/seed completed.");
        }

        private void loadVehicles() {
                List<Vehicle> vehicles = Arrays.asList(
                                // 1. Lamborghini Urus
                                createVehicleOrGet("Lamborghini", "Urus", 2024, "SUV Sport", 250000, "ESSENCE",
                                                "AUTOMATIC", 5,
                                                "Le SUV le plus rapide du monde. Puissance brute et luxe absolu.",
                                                "https://images.unsplash.com/photo-1669644383182-4161ae3d2426?q=80&w=2690&auto=format&fit=crop",
                                                "SN-2024-LU"),

                                // 2. Ferrari F8 Tributo
                                createVehicleOrGet("Ferrari", "F8 Tributo", 2023, "Supercar", 350000, "ESSENCE",
                                                "AUTOMATIC", 2,
                                                "Une icône de Maranello. V8 biturbo, 720 chevaux.",
                                                "https://images.unsplash.com/photo-1592198084033-aade902d1aae?q=80&w=2670&auto=format&fit=crop",
                                                "SN-2023-FT"),

                                // 3. Mercedes-Benz G63 AMG
                                createVehicleOrGet("Mercedes-Benz", "G63 AMG", 2024, "SUV Luxe", 200000, "ESSENCE",
                                                "AUTOMATIC", 5,
                                                "Le roi des 4x4. Imposant, bruyant, luxueux.",
                                                "https://images.unsplash.com/photo-1606220838315-056192d5e927?q=80&w=2574&auto=format&fit=crop",
                                                "SN-2024-MG"),

                                // 4. Porsche 911 GT3
                                createVehicleOrGet("Porsche", "911 GT3", 2023, "Sport", 280000, "ESSENCE", "AUTOMATIC",
                                                2,
                                                "La perfection sur circuit et sur route.",
                                                "https://images.unsplash.com/photo-1503376763036-066120622c74?q=80&w=2670&auto=format&fit=crop",
                                                "SN-2023-P9"),

                                // 5. Range Rover Autobiography
                                createVehicleOrGet("Range Rover", "Autobiography", 2024, "SUV Luxe", 180000, "DIESEL",
                                                "AUTOMATIC", 5,
                                                "Le summum du confort britannique.",
                                                "https://images.unsplash.com/photo-1605218427306-633ba87c9759?q=80&w=2670&auto=format&fit=crop",
                                                "SN-2024-RR"),

                                // 6. Audi RSQ8
                                createVehicleOrGet("Audi", "RSQ8", 2023, "SUV Sport", 190000, "ESSENCE", "AUTOMATIC", 5,
                                                "La cousine technique de l'Urus, plus discrète mais tout aussi redoutable.",
                                                "https://images.unsplash.com/photo-1614200179396-27777009e993?q=80&w=2670&auto=format&fit=crop",
                                                "SN-2023-AR"),

                                // 7. Bentley Continental GT
                                createVehicleOrGet("Bentley", "Continental GT", 2022, "Grand Tourisme", 300000,
                                                "ESSENCE",
                                                "AUTOMATIC", 4,
                                                "Voyagez en première classe, au volant.",
                                                "https://images.unsplash.com/photo-1563720223185-11003d516935?q=80&w=2670&auto=format&fit=crop",
                                                "SN-2022-BC"),

                                // 8. Rolls Royce Cullinan
                                createVehicleOrGet("Rolls Royce", "Cullinan", 2024, "Ultra Luxe", 500000, "ESSENCE",
                                                "AUTOMATIC", 4,
                                                "Le SUV le plus luxueux jamais construit.",
                                                "https://images.unsplash.com/photo-1631295868223-632658512537?q=80&w=2670&auto=format&fit=crop",
                                                "SN-2024-RC"),

                                // 9. McLaren 720S
                                createVehicleOrGet("McLaren", "720S", 2023, "Supercar", 320000, "ESSENCE", "AUTOMATIC",
                                                2,
                                                "Une aérodynamique sculptée par le vent.",
                                                "https://images.unsplash.com/photo-1621135802920-133df287f89c?q=80&w=2670&auto=format&fit=crop",
                                                "SN-2023-M7"),

                                // 10. Tesla Model X Plaid
                                createVehicleOrGet("Tesla", "Model X Plaid", 2024, "Électrique", 150000, "ELECTRIC",
                                                "AUTOMATIC", 6,
                                                "1020 chevaux, électrique, familial. Falcon Wings.",
                                                "https://images.unsplash.com/photo-1617788138017-80ad40651399?q=80&w=2670&auto=format&fit=crop",
                                                "SN-2024-TX"),

                                // 11. BMW M4 Competition
                                createVehicleOrGet("BMW", "M4 Competition", 2024, "Sport", 160000, "ESSENCE",
                                                "AUTOMATIC",
                                                4,
                                                "Le coupé sportif par excellence.",
                                                "https://images.unsplash.com/photo-1617814066861-1c390cb54f9a?q=80&w=2670&auto=format&fit=crop",
                                                "SN-2024-BM"),

                                // 12. Aston Martin DBX 707
                                createVehicleOrGet("Aston Martin", "DBX 707", 2023, "SUV Sport", 260000, "ESSENCE",
                                                "AUTOMATIC", 5,
                                                "Le SUV de James Bond. 707 chevaux.",
                                                "https://images.unsplash.com/photo-1678809415682-82f53412a84d?q=80&w=2670&auto=format&fit=crop",
                                                "SN-2023-AM"),

                                // 13. Porsche Taycan Turbo S
                                createVehicleOrGet("Porsche", "Taycan Turbo S", 2024, "Électrique", 220000, "ELECTRIC",
                                                "AUTOMATIC", 4,
                                                "L'âme de Porsche, le cœur électrique.",
                                                "https://images.unsplash.com/photo-1611016186353-9af29c778809?q=80&w=2670&auto=format&fit=crop",
                                                "SN-2024-PT"),

                                // 14. Maserati MC20
                                createVehicleOrGet("Maserati", "MC20", 2023, "Supercar", 290000, "ESSENCE", "AUTOMATIC",
                                                2,
                                                "Le trident de retour au sommet.",
                                                "https://images.unsplash.com/photo-1635783857500-1c390cb54f9a?q=80&w=2670&auto=format&fit=crop",
                                                "SN-2023-MM"),

                                // 15. Cadillac Escalade V
                                createVehicleOrGet("Cadillac", "Escalade V", 2024, "SUV XXL", 210000, "ESSENCE",
                                                "AUTOMATIC", 7,
                                                "L'Amérique en mode XXL et supercharged.",
                                                "https://images.unsplash.com/photo-1549399542-7e3f8b79c341?q=80&w=2670&auto=format&fit=crop",
                                                "SN-2024-CE"),

                                // 16. Bugatti Chiron
                                createVehicleOrGet("Bugatti", "Chiron", 2022, "Hypercar", 1500000, "ESSENCE",
                                                "AUTOMATIC",
                                                2,
                                                "L'ultime machine de vitesse.",
                                                "https://images.unsplash.com/photo-1566008885218-90abf9200ddb?q=80&w=2670&auto=format&fit=crop",
                                                "SN-2022-BCH"),

                                // 17. Peugeot 208
                                createVehicleOrGet("Peugeot", "208", 2023, "Citadine", 30000, "ESSENCE", "MANUAL", 5,
                                                "La citadine préférée des français.",
                                                "https://images.unsplash.com/photo-1541899481282-d53bffe3c35d?q=80&w=2670&auto=format&fit=crop",
                                                "SN-2023-P208"),

                                // 18. Renault Clio V
                                createVehicleOrGet("Renault", "Clio V", 2023, "Citadine", 25000, "DIESEL", "MANUAL", 5,
                                                "Polyvalente et confortable.",
                                                "https://images.unsplash.com/photo-1621007947382-bb3c3994e3fb?q=80&w=2670&auto=format&fit=crop",
                                                "SN-2023-RC5"),

                                // 19. Toyota Corolla
                                createVehicleOrGet("Toyota", "Corolla", 2022, "Berline", 40000, "HYBRID", "AUTOMATIC",
                                                5,
                                                "La fiabilité légendaire avec une consommation minimale.",
                                                "https://images.unsplash.com/photo-1623869675785-00c88206972d?q=80&w=2670&auto=format&fit=crop",
                                                "SN-2022-TC"),

                                // 20. Dacia Duster
                                createVehicleOrGet("Dacia", "Duster", 2023, "SUV Éco", 35000, "DIESEL", "MANUAL", 5,
                                                "Le SUV robuste et accessible pour l'aventure.",
                                                "https://images.unsplash.com/photo-1629898058434-754665451631?q=80&w=2670&auto=format&fit=crop",
                                                "SN-2023-DD"),

                                // 21. Hyundai Tucson
                                createVehicleOrGet("Hyundai", "Tucson", 2023, "SUV Compact", 50000, "ESSENCE",
                                                "AUTOMATIC",
                                                5,
                                                "Design futuriste et confort premium pour la famille.",
                                                "https://images.unsplash.com/photo-1632245889029-e412c6310525?q=80&w=2670&auto=format&fit=crop",
                                                "SN-2023-HT"),

                                // 22. Kia Picanto
                                createVehicleOrGet("Kia", "Picanto", 2023, "Citadine", 20000, "ESSENCE", "MANUAL", 4,
                                                "La petite citadine nerveuse pour se faufiler partout.",
                                                "https://images.unsplash.com/photo-1594535182308-8ff240fde696?q=80&w=2670&auto=format&fit=crop",
                                                "SN-2023-KP"),

                                // 23. Citroën C3
                                createVehicleOrGet("Citroën", "C3", 2023, "Citadine", 28000, "ESSENCE", "MANUAL", 5,
                                                "Le confort Citroën dans un format compact.",
                                                "https://images.unsplash.com/photo-1587630713337-7c703d152d8e?q=80&w=2670&auto=format&fit=crop",
                                                "SN-2023-CC3"),

                                // 24. Volkswagen Polo
                                createVehicleOrGet("Volkswagen", "Polo", 2022, "Citadine", 32000, "ESSENCE",
                                                "AUTOMATIC", 5,
                                                "La qualité allemande en format poche.",
                                                "https://images.unsplash.com/photo-1541899481282-d53bffe3c35d?q=80&w=2670&auto=format&fit=crop",
                                                "SN-2022-VP"));

                // Not explicitly saving list here because createVehicleOrGet saves individually
                log.info("Finished processing {} vehicles.", vehicles.size());
        }

        private Vehicle createVehicleOrGet(String brand, String model, int year, String category, double price,
                        String fuel,
                        String transmission, int seats, String desc, String imageUrl, String licensePlate) {

                // Check ONLY by license plate for existence using JpaRepository magic or
                // explicit query if needed.
                // Assuming findByLicensePlateAndIsActiveTrue works for finding existing ones.
                // If inactive ones exist, we might want to reactivate them or just ignore
                // collision.
                // For robustness, here we assume if we find it (active), we skip.
                Optional<Vehicle> existing = vehicleRepository.findByLicensePlateAndIsActiveTrue(licensePlate);

                if (existing.isPresent()) {
                        log.info("Vehicle {} {} ({}) already exists. Skipping.", brand, model, licensePlate);
                        return existing.get();
                }

                log.info("Creating vehicle {} {} ({})...", brand, model, licensePlate);
                Vehicle vehicle = Vehicle.builder()
                                .brand(brand)
                                .model(model)
                                .year(year)
                                .category(category)
                                .dailyPrice(price)
                                .licensePlate(licensePlate)
                                .status(VehicleStatus.AVAILABLE)
                                .mileage((long) (Math.random() * 5000))
                                .fuelType(fuel)
                                .transmission(transmission)
                                .seats(seats)
                                .babySeat(true)
                                .images(Arrays.asList(imageUrl))
                                .description(desc)
                                .isActive(true)
                                .build();

                return vehicleRepository.save(vehicle);
        }
}

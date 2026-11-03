
package com.parcelpicks.shipment.config;

import org.springframework.core.annotation.Order;
import com.parcelpicks.shipment.entity.Shipment;
import com.parcelpicks.shipment.repository.ShipmentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Configuration
public class ShipmentDataSeeder {

   @Bean
@Order(1)
CommandLineRunner seedShipments(ShipmentRepository repository) {
        return args -> {

            if (repository.count() > 0) {
                System.out.println("Shipments already exist. Skipping seed.");
                return;
            }

            Random random = new Random(42);
            String[] statuses = {
                "PICKUP_SCHEDULED",
                "IN_TRANSIT",
                "DELIVERED",
                "RTO"
            };

            List<Shipment> shipments = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();

            for (int i = 1; i <= 1000; i++) {

                Shipment shipment = new Shipment();

                shipment.setTrackingNumber(
                    String.format("TESTAWB%06d", i)
                );

                String status = statuses[random.nextInt(statuses.length)];

                shipment.setStatusKeyword(status);
                shipment.setStatusText(
                    "Shipment status: " + status.replace("_", " ")
                );

                int daysOld = random.nextInt(61);
                LocalDateTime createdAt = now.minusDays(daysOld + 2);

                shipment.setCreatedAt(createdAt);
                shipment.setStatusDateTime(
                    createdAt.plusDays(1)
                );

                shipments.add(shipment);
            }

            repository.saveAll(shipments);

            System.out.println("1000 sample shipments created successfully!");
        };
    }
}


package com.parcelpicks.shipment.config;

import org.springframework.core.annotation.Order;
import com.parcelpicks.shipment.entity.Shipment;
import com.parcelpicks.shipment.entity.TrackingEvent;
import com.parcelpicks.shipment.repository.ShipmentRepository;
import com.parcelpicks.shipment.repository.TrackingEventRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class TrackingEventSeeder {

    @Bean
@Order(2)
CommandLineRunner seedTrackingEvents(
        ShipmentRepository shipments,
        TrackingEventRepository events) {
        return args -> {

            if (events.count() > 0) {
                System.out.println("Tracking events already seeded.");
                return;
            }

            List<TrackingEvent> data = new ArrayList<>();

            for (Shipment shipment : shipments.findAll()) {

                String awb = shipment.getTrackingNumber();
                LocalDateTime created = shipment.getCreatedAt();
                LocalDateTime latest = shipment.getStatusDateTime();

                data.add(new TrackingEvent(
                    awb,
                    "PICKUP_SCHEDULED",
                    "Pickup scheduled",
                    created
                ));

                String status = shipment.getStatusKeyword();

                if (status.equals("PICKUP_SCHEDULED")) {
                    continue;
                }

                LocalDateTime transitTime = created.plusHours(12);

                if (latest != null && transitTime.isAfter(latest)) {
                    transitTime = latest;
                }

                data.add(new TrackingEvent(
                    awb,
                    "IN_TRANSIT",
                    "Shipment in transit",
                    transitTime
                ));

                if (status.equals("DELIVERED")
                        || status.equals("RTO")) {
                    data.add(new TrackingEvent(
                        awb,
                        status,
                        "Shipment status: " + status,
                        latest
                    ));
                }

                if (status.equals("LOST")) {
                    data.add(new TrackingEvent(
                        awb,
                        "LOST",
                        "Marked lost by POC detection rule",
                        shipment.getUpdatedAt()
                    ));
                }
            }

            events.saveAll(data);

            System.out.println(
                data.size() + " demo tracking events created!"
            );
        };
    }
}

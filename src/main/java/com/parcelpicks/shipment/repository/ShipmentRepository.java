
package com.parcelpicks.shipment.repository;

import com.parcelpicks.shipment.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    List<Shipment> findByStatusKeywordAndStatusDateTimeBefore(
        String statusKeyword,
        LocalDateTime cutoff
    );

    Optional<Shipment> findByTrackingNumber(String trackingNumber);
}

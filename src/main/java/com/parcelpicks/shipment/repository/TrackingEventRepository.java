
package com.parcelpicks.shipment.repository;

import com.parcelpicks.shipment.entity.TrackingEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TrackingEventRepository
        extends JpaRepository<TrackingEvent, Long> {

    List<TrackingEvent>
    findByTrackingNumberOrderByStatusDateTimeAsc(
        String trackingNumber
    );
}

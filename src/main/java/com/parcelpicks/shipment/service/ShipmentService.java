
package com.parcelpicks.shipment.service;

import com.parcelpicks.shipment.entity.Shipment;
import com.parcelpicks.shipment.entity.TrackingEvent;

import com.parcelpicks.shipment.repository.ShipmentRepository;
import com.parcelpicks.shipment.repository.TrackingEventRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final TrackingEventRepository eventRepository;

    public ShipmentService(
            ShipmentRepository shipmentRepository,
            TrackingEventRepository eventRepository) {

        this.shipmentRepository = shipmentRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional
    public int markLostShipments() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cutoff = now.minusDays(30);

        List<Shipment> oldShipments =
                shipmentRepository
                        .findByStatusKeywordAndStatusDateTimeBefore(
                                "IN_TRANSIT",
                                cutoff
                        );

        List<TrackingEvent> lostEvents = new ArrayList<>();

        for (Shipment shipment : oldShipments) {

            shipment.setStatusKeyword("LOST");

            shipment.setStatusText(
                    "Marked as LOST: No tracking update for over 30 days"
            );

            TrackingEvent event = new TrackingEvent(
                    shipment.getTrackingNumber(),
                    "LOST",
                    "Shipment marked LOST after 30 days without update",
                    now
            );

            lostEvents.add(event);
        }

        shipmentRepository.saveAll(oldShipments);
        eventRepository.saveAll(lostEvents);

        return oldShipments.size();
    }

    public Shipment getShipment(String trackingNumber) {

        return shipmentRepository
                .findByTrackingNumber(trackingNumber)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Shipment not found: " + trackingNumber
                        )
                );
    }

    public List<Shipment> getAllShipments() {
        return shipmentRepository.findAll();
    }
}

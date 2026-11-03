
package com.parcelpicks.shipment.controller;

import com.parcelpicks.shipment.entity.Shipment;
import com.parcelpicks.shipment.entity.TrackingEvent;
import com.parcelpicks.shipment.repository.TrackingEventRepository;
import com.parcelpicks.shipment.service.ShipmentService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {

    private final ShipmentService service;
    private final TrackingEventRepository eventRepository;

    // Constructor Injection
    public ShipmentController(
            ShipmentService service,
            TrackingEventRepository eventRepository) {

        this.service = service;
        this.eventRepository = eventRepository;
    }

    // 1. Get all shipments
    @GetMapping
    public List<Shipment> getAllShipments() {
        return service.getAllShipments();
    }

    // 2. Get shipment by AWB number
    @GetMapping("/{trackingNumber}")
    public ResponseEntity<?> getShipment(
            @PathVariable String trackingNumber) {

        try {
            Shipment shipment = service.getShipment(trackingNumber);
            return ResponseEntity.ok(shipment);

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Shipment not found"));
        }
    }

    // 3. Mark old IN_TRANSIT shipments as LOST
    @PostMapping("/mark-lost")
    public Map<String, Object> markLostShipments() {

        int count = service.markLostShipments();

        return Map.of(
                "success", true,
                "message", "Lost detection completed",
                "markedLost", count
        );
    }

    // 4. Get complete tracking event history by AWB
    @GetMapping("/{trackingNumber}/events")
    public ResponseEntity<?> getTrackingEvents(
            @PathVariable String trackingNumber) {

        try {
            // Verify that shipment exists
            service.getShipment(trackingNumber);

            List<TrackingEvent> events =
                    eventRepository
                            .findByTrackingNumberOrderByStatusDateTimeAsc(
                                    trackingNumber
                            );

            return ResponseEntity.ok(events);

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Shipment not found"));
        }
    }
}

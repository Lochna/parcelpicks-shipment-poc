
package com.parcelpicks.shipment.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tracking_events")
public class TrackingEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tracking_number", nullable = false)
    private String trackingNumber;

    @Column(name = "status_keyword", nullable = false)
    private String statusKeyword;

    @Column(name = "status_text")
    private String statusText;

    @Column(name = "status_date_time", nullable = false)
    private LocalDateTime statusDateTime;

    public TrackingEvent() {}

    public TrackingEvent(String trackingNumber,
                         String statusKeyword,
                         String statusText,
                         LocalDateTime statusDateTime) {
        this.trackingNumber = trackingNumber;
        this.statusKeyword = statusKeyword;
        this.statusText = statusText;
        this.statusDateTime = statusDateTime;
    }

    public Long getId() {
        return id;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public String getStatusKeyword() {
        return statusKeyword;
    }

    public String getStatusText() {
        return statusText;
    }

    public LocalDateTime getStatusDateTime() {
        return statusDateTime;
    }
}

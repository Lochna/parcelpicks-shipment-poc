
# ParcelPicks Shipment Tracking POC

## Project Overview
A Spring Boot and MySQL-based Proof of Concept for managing shipment tracking records, retrieving AWB-based tracking history, and detecting shipments stuck in transit for more than 30 days.

## Technology Stack
- Java 25
- Spring Boot 4.0.9
- MySQL 8.4
- Maven Wrapper
- Spring Data JPA / Hibernate
- REST APIs

## Features
1. Automatic database table generation using JPA.
2. Generation of 1,000 synthetic shipment records.
3. Synthetic tracking-event history.
4. Shipment lookup using AWB number.
5. Lost detection for IN_TRANSIT shipments with no courier status update for over 30 days.
6. LOST event insertion into tracking history.
7. HTTP 404 handling for unknown AWB numbers.

## Database Setup

Create a MySQL database and application user:

```sql
CREATE DATABASE parcelpicks_poc;

CREATE USER 'parcelpicks_user'@'localhost'
IDENTIFIED BY 'YOUR_PASSWORD';

GRANT ALL PRIVILEGES ON parcelpicks_poc.*
TO 'parcelpicks_user'@'localhost';
```

Replace YOUR_PASSWORD with your own secure password.

## Running the Application

Open PowerShell in the project directory.

Set the database password:

```powershell
$securePassword = Read-Host "MySQL Password" -AsSecureString
$env:DB_PASSWORD = [System.Net.NetworkCredential]::new("", $securePassword).Password
```

Start the application:

```powershell
.\mvnw.cmd spring-boot:run
```

Default server: http://localhost:8080

JPA automatically creates the required tables in the existing database.

## REST API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | /api/shipments | Retrieve all shipments |
| GET | /api/shipments/{awb} | Retrieve shipment by AWB |
| GET | /api/shipments/{awb}/events | Retrieve tracking history |
| POST | /api/shipments/mark-lost | Detect and mark old shipments as LOST |

## Lost Detection Logic

A shipment is eligible when:

- Current status is IN_TRANSIT.
- Last courier status timestamp is older than 30 days.

The API updates its status to LOST and creates a tracking-history event in one transaction.

Already LOST shipments are not processed again.

## Testing

Get a sample shipment:

```powershell
Invoke-RestMethod -Method GET -Uri "http://localhost:8080/api/shipments/TESTAWB000001"
```

Get tracking history:

```powershell
Invoke-RestMethod -Method GET -Uri "http://localhost:8080/api/shipments/TESTAWB000001/events"
```

Run Lost detection:

```powershell
Invoke-RestMethod -Method POST -Uri "http://localhost:8080/api/shipments/mark-lost"
```

## Verified Local Results

- 1,000 synthetic shipment records
- 2,456 generated tracking events
- 134 shipments initially detected as LOST
- Second Lost detection call returned 0 additional records
- AWB lookup and tracking history verified
- Invalid AWB returns HTTP 404
- Maven build completed successfully

Results are from the development database and may vary in a fresh installation.

## Limitations

- Uses synthetic data rather than live courier APIs.
- Lost detection requires manual API invocation.
- No authentication or authorization.
- No automatic courier event ingestion.
- Advanced concurrency handling and batch processing are not implemented.
- The 30-day rule is a POC rule, not confirmation of physical parcel loss.

## Notes

This project is intended for demonstration and technical validation, not production deployment.
```

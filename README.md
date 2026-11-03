# 🚕 TaxiGo — Desktop Taxi Dispatch & Ordering System

[![Java](https://img.shields.io/badge/Language-Java%2021-ED8B00?style=flat&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![JavaFX](https://img.shields.io/badge/UI-JavaFX-orange.svg)](https://openjfx.io/)
[![MySQL](https://img.shields.io/badge/Database-MySQL%208%2B-4479A1?style=flat&logo=mysql&logoColor=white)](https://mysql.com)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

A multi-role desktop taxi ordering and dispatch management system built with **Java 21, JavaFX, and MySQL JDBC**. Provides distinct interactive portals for **Passengers**, **Drivers**, and **Fleet Administrators**.

---

## 📸 Desktop Application Screenshots

<div align="center">
  <img src="docs/screenshots/taxigo_passenger_portal.png" alt="Passenger Booking & Destination Search" width="90%" />
  <p><i>Figure 1: Passenger Portal with destination search, real-time taxi availability, and booking history.</i></p>
</div>

<div align="center">
  <img src="docs/screenshots/taxigo_admin_dispatch.png" alt="Admin Fleet Dispatch & Queue Analytics" width="90%" />
  <p><i>Figure 2: Admin Dashboard displaying real-time fleet metrics, driver routes, and automatic departure controls.</i></p>
</div>

> **Tip:** Place your JavaFX screenshots in `docs/screenshots/taxigo_passenger_portal.png` and `docs/screenshots/taxigo_admin_dispatch.png`.

---

## ✨ Features by Role

### 🧍 Passenger Portal
- Account registration with duplicate prevention.
- Search taxis filtered dynamically by destination and availability (`available = TRUE`).
- Real-time ride booking and personal booking history ledger.

### 🚗 Driver Portal
- Vehicle registration linked to driver credentials with duplicate license plate protection.
- Real-time queue view monitoring awaiting passengers for assigned routes.
- Live vehicle status toggle (🟢 Available / 🔴 En Route).

### 🔐 Admin & Dispatch Operations
- **Live Fleet KPI Bar:** Total users, registered vehicles, active drivers, passengers in queue, and completed departures.
- **Auto-Departure Automation:** Triggers automated vehicle dispatch whenever 16 or more passengers share a destination and a taxi is available.
- **Manual Override:** One-click manual departure authorization for custom fleet routing.

---

## 🔒 Security & Architecture

- **Credential Security:** Passwords stored as SHA-256 cryptographic hashes.
- **Role Isolation:** Strict role separation preventing unauthorized role escalation during sign-up.
- **Centralized Connectivity:** Thread-safe connection factory pattern implemented in `DBConfig.java`.

---

## 🚀 How to Run

### Prerequisites
- JDK 21+
- JavaFX SDK 21+
- MySQL 8.0+ server

### Setup
```bash
# 1. Clone repository
git clone https://github.com/aprotovic/TaxiOrderingSystem.git
cd TaxiOrderingSystem

# 2. Import database schema
mysql -u root -p < Database/TaxiOrderingSystem.sql

# 3. Configure credentials in src/com/taxiapp/DBConfig.java
# 4. Compile and launch via IDE (Eclipse / IntelliJ) or Maven/Gradle with JavaFX
```

---

## 📄 License
MIT License • Built by [aprotovic](https://github.com/aprotovic).

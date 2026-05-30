-- ============================================================
--  TaxiOrderingSystem — Database Setup Script
--  Supports: MySQL 8+
-- ============================================================

DROP DATABASE IF EXISTS TaxiOrderingSystem;
CREATE DATABASE IF NOT EXISTS TaxiOrderingSystem
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;
USE TaxiOrderingSystem;

-- ── Users ─────────────────────────────────────────────────────────────────────
-- Password column stores SHA-256 hex (64 chars). Upgrade to BCrypt for production.
CREATE TABLE IF NOT EXISTS Users (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    username   VARCHAR(100) UNIQUE NOT NULL,
    password   VARCHAR(64)  NOT NULL,  -- SHA-256 hex (64 chars)
    role       ENUM('admin', 'passenger', 'driver') NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ── Taxis ─────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS Taxis (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    license_plate VARCHAR(50)  UNIQUE NOT NULL,
    destination   VARCHAR(255) NOT NULL,
    available     BOOLEAN      DEFAULT TRUE,
    driver_id     INT,
    created_at    DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (driver_id) REFERENCES Users(id) ON DELETE SET NULL
);

-- ── Passengers ────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS Passengers (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    destination VARCHAR(255) NOT NULL,
    user_id     INT,
    taxi_id     INT,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE,
    FOREIGN KEY (taxi_id) REFERENCES Taxis(id) ON DELETE SET NULL
);

-- ── Departures ────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS Departures (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    taxi_id        INT,
    passenger_id   INT,
    departure_time DATETIME,
    FOREIGN KEY (taxi_id)      REFERENCES Taxis(id)      ON DELETE CASCADE,
    FOREIGN KEY (passenger_id) REFERENCES Passengers(id) ON DELETE CASCADE
);

-- ── Seed: default admin account ───────────────────────────────────────────────
-- Default credentials: username=admin, password=admin123
-- SHA-256 of 'admin123' = 240be518fabd2724ddb6f04eeb1da5967448d7e831186422fc5dc7abc3a7cd84
INSERT INTO Users (name, username, password, role) VALUES
    ('System Admin', 'admin',
     '240be518fabd2724ddb6f04eeb1da5967448d7e831186422fc5dc7abc3a7cd84',
     'admin');

-- ── Verification ──────────────────────────────────────────────────────────────
SELECT CONCAT('✓ Setup complete. Users: ', COUNT(*)) AS status FROM Users;

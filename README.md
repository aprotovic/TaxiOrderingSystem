# 🚕 TaxiGo — Taxi Ordering System

A modern **JavaFX desktop application** for managing taxi ordering across multiple user roles: **Passengers**, **Drivers**, and **Admins**. Built with Java 21, JavaFX, MySQL, and JDBC.

---

## ✨ Features

### 🧍 Passenger
- Register & log in securely
- Search available taxis by destination (filtered to `available = TRUE`)
- Book a taxi with duplicate-booking prevention
- View personal booking history in-app

### 🚗 Driver
- Register taxis linked to your driver account
- Duplicate license-plate protection
- Real-time view of the passenger queue for your route(s)
- See taxi availability status (🟢 / 🔴)

### 🔐 Admin
- **Live stats bar** — total users, taxis, available taxis, passengers, departures
- View all taxis with driver names and destinations
- View departure history with passenger names and timestamps
- Auto-assign passengers to available taxis matching their destination
- Trigger auto-departure when ≥ 16 passengers share a destination
- Force manual departure for any taxi

---

## 🔒 Security Improvements
- **Password hashing**: passwords are stored as SHA-256 hashes (upgrade path to BCrypt documented in `PasswordUtil.java`)
- **Role restriction**: only `passenger` and `driver` roles can self-register (admin accounts are created directly in the DB)
- **Duplicate username check** on sign-up
- **Centralized DB config** in `DBConfig.java` — one place to update credentials

---

## 🧱 Tech Stack

| Layer       | Technology               |
|-------------|--------------------------|
| Language    | Java 21 (JDK 21)         |
| UI          | JavaFX                   |
| Database    | MySQL 8+                 |
| Connectivity| JDBC                     |
| IDE         | Eclipse                  |

---

## 🗂 Project Structure

```
TaxiOrderingSystem/
├── src/com/taxiapp/
│   ├── TaxiOrderingSystemApp.java   ← Entry point
│   ├── DBConfig.java                ← DB connection factory
│   ├── AppStyles.java               ← Centralized UI styles
│   ├── PasswordUtil.java            ← SHA-256 password hashing
│   ├── SignInPage.java
│   ├── SignUpPage.java
│   ├── PassengerPage.java
│   ���── DriverPage.java
│   └── AdminPage.java
├── Database/
│   └── TaxiOrderingSystem.sql       ← Schema + seed admin account
└── Documentation/
    └── README.md
```

---

## 🛠 How to Run

### Prerequisites
- JDK 21+
- JavaFX SDK (download from [gluonhq.com/products/javafx](https://gluonhq.com/products/javafx))
- MySQL 8+ server running
- MySQL JDBC connector JAR

### Steps

1. **Clone the repository**
   ```bash
   git clone <your-repo-url>
   ```

2. **Import into Eclipse**
   - *File → Import → Existing Projects into Workspace*
   - Add the JavaFX SDK and MySQL JDBC JAR to the Build Path

3. **Set up the database**
   ```sql
   -- Run in MySQL Workbench or any MySQL client:
   source /path/to/TaxiOrderingSystem.sql
   ```
   This creates the schema with a default admin account. **Change the admin password immediately after setup.**

4. **Update database credentials** in `DBConfig.java`
   ```java
   private static final String DB_URL  = "your_database_url";
   private static final String DB_USER = "your_db_user";
   private static final String DB_PASS = "your_secure_password";
   ```

5. **Run** `TaxiOrderingSystemApp.java`

---

## 💡 Notes
- The MySQL server must be running before launching the app.
- Only `passenger` and `driver` roles can be selected during sign-up. Admin access requires a manually inserted DB record.
- The auto-departure rule triggers when **16 or more passengers** share the same destination and an available taxi exists for that route.
- **Never commit actual database credentials to version control.** Use environment variables or a `.env` file (added to `.gitignore`) for sensitive data.

---

## 📸 Roles at a Glance

| Role      | Access Method         |
|-----------|------------------------|
| Admin     | Direct database insert (created during initial setup) |
| Passenger | Register via Sign Up  |
| Driver    | Register via Sign Up  |

package com.taxiapp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;

public class DriverPage {

    private final String currentUsername;
    private final int    currentUserId;

    public DriverPage(String username, int userId) {
        this.currentUsername = username;
        this.currentUserId   = userId;
    }

    public void showDriverPage(Stage stage) {

        // ── Root ──────────────────────────────────────────────────────────────
        BorderPane root = new BorderPane();
        root.setStyle(AppStyles.SCENE_BG);

        // ── Header ────────────────────────────────────────────────────────────
        HBox header = buildHeader(stage);
        root.setTop(header);

        // ── Center card ───────────────────────────────────────────────────────
        VBox card = new VBox(14);
        card.setAlignment(Pos.TOP_LEFT);
        card.setStyle(AppStyles.CARD);
        card.setMaxWidth(520);

        Label sectionTitle = new Label("Driver Dashboard");
        sectionTitle.setStyle(AppStyles.SECTION_HEADER);

        // ── Register Taxi ─────────────────────────────────────────────────────
        Label regHeader = new Label("REGISTER YOUR TAXI");
        regHeader.setStyle(AppStyles.LABEL);

        Label plateLabel = new Label("LICENSE PLATE");
        plateLabel.setStyle(AppStyles.LABEL);
        TextField licensePlateField = new TextField();
        licensePlateField.setPromptText("e.g. ET-3-12345");
        licensePlateField.setStyle(AppStyles.TEXT_FIELD);
        licensePlateField.setMaxWidth(Double.MAX_VALUE);

        Label destLabel = new Label("DESTINATION");
        destLabel.setStyle(AppStyles.LABEL);
        TextField destinationField = new TextField();
        destinationField.setPromptText("e.g. Airport, Bole, Merkato");
        destinationField.setStyle(AppStyles.TEXT_FIELD);
        destinationField.setMaxWidth(Double.MAX_VALUE);

        Button registerButton = new Button("🚕  Register Taxi");
        registerButton.setMaxWidth(Double.MAX_VALUE);
        AppStyles.addHover(registerButton, AppStyles.BTN_SUCCESS, AppStyles.BTN_SUCCESS);

        Label regStatus = new Label("");
        regStatus.setWrapText(true);
        regStatus.setStyle("-fx-text-fill: #2ecc71; -fx-font-size: 12px;");

        Separator sep = new Separator();
        sep.setStyle(AppStyles.SEPARATOR);

        // ── My Taxis ─────────────────────────────────────────────────────────
        Label myTaxisLabel = new Label("MY REGISTERED TAXIS");
        myTaxisLabel.setStyle(AppStyles.LABEL);

        ListView<String> myTaxisList = new ListView<>();
        myTaxisList.setStyle(AppStyles.LIST_VIEW);
        myTaxisList.setPrefHeight(130);
        myTaxisList.setPlaceholder(new Label("No taxis registered yet."));

        Button refreshTaxisBtn = new Button("↻  Refresh My Taxis");
        refreshTaxisBtn.setStyle(AppStyles.BTN_SECONDARY);
        AppStyles.addHover(refreshTaxisBtn, AppStyles.BTN_SECONDARY, AppStyles.BTN_SECONDARY_HOVER);

        Separator sep2 = new Separator();

        // ── Passenger Queue ───────────────────────────────────────────────────
        Label passengersHeader = new Label("PASSENGER QUEUE (BY DESTINATION)");
        passengersHeader.setStyle(AppStyles.LABEL);

        ListView<String> passengersList = new ListView<>();
        passengersList.setStyle(AppStyles.LIST_VIEW);
        passengersList.setPrefHeight(150);
        passengersList.setPlaceholder(new Label("No passengers waiting."));

        Button viewPassengersBtn = new Button("👥  View Passenger Queue");
        viewPassengersBtn.setStyle(AppStyles.BTN_SECONDARY);
        AppStyles.addHover(viewPassengersBtn, AppStyles.BTN_SECONDARY, AppStyles.BTN_SECONDARY_HOVER);

        card.getChildren().addAll(
                sectionTitle, regHeader,
                plateLabel, licensePlateField,
                destLabel, destinationField,
                registerButton, regStatus,
                sep,
                myTaxisLabel, myTaxisList, refreshTaxisBtn,
                sep2,
                passengersHeader, passengersList, viewPassengersBtn
        );

        StackPane center = new StackPane(card);
        center.setPadding(new Insets(24));
        root.setCenter(center);

        // ── Actions ───────────────────────────────────────────────────────────
        registerButton.setOnAction(e -> {
            regStatus.setText("");
            String plate = licensePlateField.getText().trim();
            String dest  = destinationField.getText().trim();
            if (plate.isEmpty() || dest.isEmpty()) {
                regStatus.setStyle("-fx-text-fill: #e94560; -fx-font-size: 12px;");
                regStatus.setText("⚠ Both fields are required.");
                return;
            }
            registerTaxi(plate, dest, regStatus);
            if (regStatus.getText().startsWith("✅")) {
                licensePlateField.clear();
                destinationField.clear();
                loadMyTaxis(myTaxisList);
            }
        });

        refreshTaxisBtn.setOnAction(e -> loadMyTaxis(myTaxisList));
        viewPassengersBtn.setOnAction(e -> viewPassengers(passengersList));

        // Initial load
        loadMyTaxis(myTaxisList);

        Scene scene = new Scene(root, 620, 720);
        stage.setTitle("TaxiGo — Driver Dashboard");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }

    // ── Header ────────────────────────────────────────────────────────────────
    private HBox buildHeader(Stage stage) {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(14, 20, 14, 20));
        header.setStyle("-fx-background-color: #16213e; -fx-border-color: #2a2a5a; -fx-border-width: 0 0 1 0;");

        Label appName = new Label("🚕 TaxiGo");
        appName.setStyle(AppStyles.TITLE + "-fx-font-size: 18px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userInfo = new Label("🚗 " + currentUsername + "  |  Driver");
        userInfo.setStyle(AppStyles.SUBTITLE);

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle(AppStyles.LINK_BUTTON);
        logoutBtn.setOnAction(e -> new SignInPage().showSignInForm(stage));

        header.getChildren().addAll(appName, spacer, userInfo, logoutBtn);
        return header;
    }

    // ── DB Helpers ────────────────────────────────────────────────────────────
    private void registerTaxi(String licensePlate, String destination, Label statusLabel) {
        try (Connection conn = DBConfig.getConnection()) {
            // Check for duplicate plate
            PreparedStatement check = conn.prepareStatement(
                    "SELECT id FROM Taxis WHERE license_plate = ?");
            check.setString(1, licensePlate);
            if (check.executeQuery().next()) {
                statusLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-size: 12px;");
                statusLabel.setText("⚠ A taxi with this plate is already registered.");
                return;
            }

            String query = "INSERT INTO Taxis (license_plate, destination, available, driver_id) VALUES (?, ?, TRUE, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, licensePlate);
            stmt.setString(2, destination);
            stmt.setInt(3, currentUserId);

            if (stmt.executeUpdate() > 0) {
                statusLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-size: 12px;");
                statusLabel.setText("✅ Taxi " + licensePlate + " registered for " + destination + "!");
            } else {
                statusLabel.setStyle("-fx-text-fill: #e94560; -fx-font-size: 12px;");
                statusLabel.setText("✗ Registration failed.");
            }
        } catch (SQLException e) {
            statusLabel.setStyle("-fx-text-fill: #e94560; -fx-font-size: 12px;");
            statusLabel.setText("DB error: " + e.getMessage());
        }
    }

    private void loadMyTaxis(ListView<String> list) {
        list.getItems().clear();
        try (Connection conn = DBConfig.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT license_plate, destination, available FROM Taxis WHERE driver_id = ?");
            stmt.setInt(1, currentUserId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String status = rs.getBoolean("available") ? "🟢 Available" : "🔴 Departed";
                list.getItems().add(
                        rs.getString("license_plate") +
                        "  →  " + rs.getString("destination") +
                        "  |  " + status
                );
            }
        } catch (SQLException e) {
            list.getItems().add("Error: " + e.getMessage());
        }
    }

    private void viewPassengers(ListView<String> list) {
        list.getItems().clear();
        try (Connection conn = DBConfig.getConnection()) {
            // Show passengers grouped by destination that match the driver's registered destinations
            String query =
                "SELECT p.name, p.destination, COUNT(*) OVER (PARTITION BY p.destination) AS count_in_dest " +
                "FROM Passengers p " +
                "WHERE p.destination IN (SELECT destination FROM Taxis WHERE driver_id = ?) " +
                "ORDER BY p.destination";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, currentUserId);
            ResultSet rs = stmt.executeQuery();
            boolean any = false;
            while (rs.next()) {
                list.getItems().add(
                        "👤 " + rs.getString("name") +
                        "  →  " + rs.getString("destination") +
                        "  (" + rs.getInt("count_in_dest") + " waiting)"
                );
                any = true;
            }
            if (!any) {
                list.getItems().add("No passengers waiting for your route(s).");
            }
        } catch (SQLException e) {
            list.getItems().add("Error: " + e.getMessage());
        }
    }
}

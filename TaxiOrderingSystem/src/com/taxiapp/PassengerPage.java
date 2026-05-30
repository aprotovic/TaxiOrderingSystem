package com.taxiapp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;

public class PassengerPage {

    private final String currentUsername;
    private final int    currentUserId;

    public PassengerPage(String username, int userId) {
        this.currentUsername = username;
        this.currentUserId   = userId;
    }

    public void showPassengerPage(Stage stage) {

        // ── Root ──────────────────────────────────────────────────────────────
        BorderPane root = new BorderPane();
        root.setStyle(AppStyles.SCENE_BG);

        // ── Top header bar ────────────────────────────────────────────────────
        HBox header = buildHeader(stage);
        root.setTop(header);

        // ── Center content card ───────────────────────────────────────────────
        VBox card = new VBox(14);
        card.setAlignment(Pos.TOP_LEFT);
        card.setStyle(AppStyles.CARD);
        card.setMaxWidth(500);

        Label sectionTitle = new Label("Book a Taxi");
        sectionTitle.setStyle(AppStyles.SECTION_HEADER);

        // Destination input
        Label destLabel = new Label("DESTINATION");
        destLabel.setStyle(AppStyles.LABEL);
        TextField destinationField = new TextField();
        destinationField.setPromptText("e.g. Airport, Downtown, Bole");
        destinationField.setStyle(AppStyles.TEXT_FIELD);
        destinationField.setMaxWidth(Double.MAX_VALUE);

        // Buttons row
        HBox btnRow = new HBox(12);
        Button showTaxisButton = new Button("🔍  Search Taxis");
        showTaxisButton.setStyle(AppStyles.BTN_SECONDARY);
        AppStyles.addHover(showTaxisButton, AppStyles.BTN_SECONDARY, AppStyles.BTN_SECONDARY_HOVER);

        Button bookButton = new Button("✅  Book Selected Taxi");
        bookButton.setStyle(AppStyles.BTN_PRIMARY);
        AppStyles.addHover(bookButton, AppStyles.BTN_PRIMARY, AppStyles.BTN_PRIMARY_HOVER);
        btnRow.getChildren().addAll(showTaxisButton, bookButton);

        // Status label
        Label statusLabel = new Label("");
        statusLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-size: 12px;");
        statusLabel.setWrapText(true);

        // Taxi list
        Label availableLabel = new Label("AVAILABLE TAXIS");
        availableLabel.setStyle(AppStyles.LABEL);
        ListView<String> taxiListView = new ListView<>();
        taxiListView.setStyle(AppStyles.LIST_VIEW);
        taxiListView.setPrefHeight(180);
        taxiListView.setPlaceholder(new Label("Search for a destination to see taxis."));

        // My bookings section
        Label myBookingsLabel = new Label("MY BOOKINGS");
        myBookingsLabel.setStyle(AppStyles.LABEL);
        ListView<String> myBookingsList = new ListView<>();
        myBookingsList.setStyle(AppStyles.LIST_VIEW);
        myBookingsList.setPrefHeight(120);
        myBookingsList.setPlaceholder(new Label("No bookings yet."));

        Button refreshBookingsBtn = new Button("↻  Refresh My Bookings");
        refreshBookingsBtn.setStyle(AppStyles.BTN_SECONDARY);
        AppStyles.addHover(refreshBookingsBtn, AppStyles.BTN_SECONDARY, AppStyles.BTN_SECONDARY_HOVER);

        card.getChildren().addAll(
                sectionTitle,
                destLabel, destinationField,
                btnRow, statusLabel,
                availableLabel, taxiListView,
                new Separator(),
                myBookingsLabel, myBookingsList,
                refreshBookingsBtn
        );

        StackPane center = new StackPane(card);
        center.setPadding(new Insets(24));
        root.setCenter(center);

        // ── Actions ───────────────────────────────────────────────────────────
        showTaxisButton.setOnAction(e -> {
            statusLabel.setText("");
            String dest = destinationField.getText().trim();
            taxiListView.getItems().clear();
            if (dest.isEmpty()) {
                statusLabel.setStyle("-fx-text-fill: #e94560; -fx-font-size: 12px;");
                statusLabel.setText("⚠ Please enter a destination.");
                return;
            }
            fetchAvailableTaxis(dest, taxiListView, statusLabel);
        });

        bookButton.setOnAction(e -> {
            String dest     = destinationField.getText().trim();
            String selected = taxiListView.getSelectionModel().getSelectedItem();
            if (dest.isEmpty()) {
                statusLabel.setStyle("-fx-text-fill: #e94560; -fx-font-size: 12px;");
                statusLabel.setText("⚠ Please enter a destination first.");
                return;
            }
            if (selected == null) {
                statusLabel.setStyle("-fx-text-fill: #e94560; -fx-font-size: 12px;");
                statusLabel.setText("⚠ Please select a taxi from the list.");
                return;
            }
            // Extract taxi ID from list item "Taxi #ID | ..."
            int taxiId = extractTaxiId(selected);
            registerPassengerForTaxi(dest, taxiId, statusLabel);
        });

        refreshBookingsBtn.setOnAction(e -> loadMyBookings(myBookingsList));

        // Initial load
        loadMyBookings(myBookingsList);

        Scene scene = new Scene(root, 620, 680);
        stage.setTitle("TaxiGo — Passenger Dashboard");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }

    // ── Helper: Header ────────────────────────────────────────────────────────
    private HBox buildHeader(Stage stage) {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(14, 20, 14, 20));
        header.setStyle("-fx-background-color: #16213e; -fx-border-color: #2a2a5a; -fx-border-width: 0 0 1 0;");

        Label appName = new Label("🚕 TaxiGo");
        appName.setStyle(AppStyles.TITLE + "-fx-font-size: 18px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userInfo = new Label("👤 " + currentUsername + "  |  Passenger");
        userInfo.setStyle(AppStyles.SUBTITLE);

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle(AppStyles.LINK_BUTTON);
        logoutBtn.setOnAction(e -> new SignInPage().showSignInForm(stage));

        header.getChildren().addAll(appName, spacer, userInfo, logoutBtn);
        return header;
    }

    // ── DB Helpers ────────────────────────────────────────────────────────────
    private void fetchAvailableTaxis(String destination, ListView<String> listView, Label statusLabel) {
        try (Connection conn = DBConfig.getConnection()) {
            String query = "SELECT id, license_plate, destination FROM Taxis WHERE destination = ? AND available = TRUE";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, destination);
            ResultSet rs = stmt.executeQuery();

            boolean found = false;
            while (rs.next()) {
                listView.getItems().add(
                        "Taxi #" + rs.getInt("id") +
                        " | Plate: " + rs.getString("license_plate") +
                        " | → " + rs.getString("destination")
                );
                found = true;
            }

            if (found) {
                statusLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-size: 12px;");
                statusLabel.setText("✓ " + listView.getItems().size() + " taxi(s) found.");
            } else {
                statusLabel.setStyle("-fx-text-fill: #e94560; -fx-font-size: 12px;");
                statusLabel.setText("No available taxis for " + destination + " right now.");
            }
        } catch (SQLException e) {
            statusLabel.setStyle("-fx-text-fill: #e94560; -fx-font-size: 12px;");
            statusLabel.setText("DB error: " + e.getMessage());
        }
    }

    private void registerPassengerForTaxi(String destination, int taxiId, Label statusLabel) {
        try (Connection conn = DBConfig.getConnection()) {
            // Prevent double-booking to same destination
            PreparedStatement check = conn.prepareStatement(
                    "SELECT id FROM Passengers WHERE user_id = ? AND destination = ?");
            check.setInt(1, currentUserId);
            check.setString(2, destination);
            if (check.executeQuery().next()) {
                statusLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-size: 12px;");
                statusLabel.setText("⚠ You already have a booking to " + destination + ".");
                return;
            }

            String query = "INSERT INTO Passengers (name, destination, user_id, taxi_id) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, currentUsername);
            stmt.setString(2, destination);
            stmt.setInt(3, currentUserId);
            stmt.setInt(4, taxiId == -1 ? 0 : taxiId);

            if (stmt.executeUpdate() > 0) {
                statusLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-size: 12px;");
                statusLabel.setText("✅ Booking confirmed! Taxi to " + destination + ".");
            } else {
                statusLabel.setStyle("-fx-text-fill: #e94560; -fx-font-size: 12px;");
                statusLabel.setText("✗ Booking failed. Please try again.");
            }
        } catch (SQLException e) {
            statusLabel.setStyle("-fx-text-fill: #e94560; -fx-font-size: 12px;");
            statusLabel.setText("DB error: " + e.getMessage());
        }
    }

    private void loadMyBookings(ListView<String> list) {
        list.getItems().clear();
        try (Connection conn = DBConfig.getConnection()) {
            String query =
                "SELECT p.destination, p.taxi_id, t.license_plate " +
                "FROM Passengers p " +
                "LEFT JOIN Taxis t ON p.taxi_id = t.id " +
                "WHERE p.user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, currentUserId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String plate = rs.getString("license_plate");
                list.getItems().add(
                        "→ " + rs.getString("destination") +
                        "  | Taxi: " + (plate != null ? plate : "Pending Assignment")
                );
            }
        } catch (SQLException e) {
            list.getItems().add("Error loading bookings: " + e.getMessage());
        }
    }

    /** Parses "Taxi #12 | ..." → 12, or -1 on failure. */
    private int extractTaxiId(String item) {
        try {
            String part = item.split("\\|")[0].replace("Taxi #", "").trim();
            return Integer.parseInt(part);
        } catch (Exception ex) {
            return -1;
        }
    }
}

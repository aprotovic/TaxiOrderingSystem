package com.taxiapp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.*;

public class AdminPage {

    public void showAdminPage(Stage stage) {

        // ── Root ──────────────────────────────────────────────────────────────
        BorderPane root = new BorderPane();
        root.setStyle(AppStyles.SCENE_BG);

        // ── Header ────────────────────────────────────────────────────────────
        HBox header = buildHeader(stage);
        root.setTop(header);

        // ── Stats bar ─────────────────────────────────────────────────────────
        HBox statsBar = buildStatsBar();
        root.setCenter(buildMainContent(stage, statsBar));

        Scene scene = new Scene(root, 720, 760);
        stage.setTitle("TaxiGo — Admin Dashboard");
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

        Label badge = new Label("🔐  Administrator");
        badge.setStyle("-fx-background-color: #e94560; -fx-text-fill: white; " +
                       "-fx-padding: 4 10 4 10; -fx-background-radius: 12; -fx-font-size: 12px;");

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle(AppStyles.LINK_BUTTON);
        logoutBtn.setOnAction(e -> new SignInPage().showSignInForm(stage));

        header.getChildren().addAll(appName, spacer, badge, logoutBtn);
        HBox.setMargin(logoutBtn, new Insets(0, 0, 0, 12));
        return header;
    }

    // ── Stats bar ─────────────────────────────────────────────────────────────
    private HBox buildStatsBar() {
        HBox bar = new HBox(16);
        bar.setPadding(new Insets(16, 20, 0, 20));
        bar.setAlignment(Pos.CENTER_LEFT);

        try (Connection conn = DBConfig.getConnection()) {
            bar.getChildren().add(statCard("👥 Total Users",  queryCount(conn, "SELECT COUNT(*) FROM Users")));
            bar.getChildren().add(statCard("🚕 Total Taxis",  queryCount(conn, "SELECT COUNT(*) FROM Taxis")));
            bar.getChildren().add(statCard("🟢 Available",    queryCount(conn, "SELECT COUNT(*) FROM Taxis WHERE available = TRUE")));
            bar.getChildren().add(statCard("🧍 Passengers",   queryCount(conn, "SELECT COUNT(*) FROM Passengers")));
            bar.getChildren().add(statCard("✈ Departures",   queryCount(conn, "SELECT COUNT(*) FROM Departures")));
        } catch (SQLException e) {
            bar.getChildren().add(new Label("Could not load stats: " + e.getMessage()));
        }
        return bar;
    }

    private VBox statCard(String label, int value) {
        VBox card = new VBox(4);
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: #16213e; -fx-background-radius: 10; " +
                      "-fx-padding: 12 20 12 20; -fx-min-width: 110;");
        Label val = new Label(String.valueOf(value));
        val.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #e94560;");
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #a0a0b0;");
        card.getChildren().addAll(val, lbl);
        return card;
    }

    private int queryCount(Connection conn, String sql) {
        try (PreparedStatement st = conn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            return -1;
        }
    }

    // ── Main Content ──────────────────────────────────────────────────────────
    private ScrollPane buildMainContent(Stage stage, HBox statsBar) {
        VBox content = new VBox(20);
        content.setPadding(new Insets(0, 20, 24, 20));

        content.getChildren().add(statsBar);

        // ── Action buttons grid ───────────────────────────────────────────────
        VBox card = new VBox(14);
        card.setStyle(AppStyles.CARD);

        Label sectionTitle = new Label("⚙  Admin Controls");
        sectionTitle.setStyle(AppStyles.SECTION_HEADER);

        // Taxis section
        Label taxisLabel = new Label("TAXI MANAGEMENT");
        taxisLabel.setStyle(AppStyles.LABEL);

        ListView<String> taxiListView = new ListView<>();
        taxiListView.setStyle(AppStyles.LIST_VIEW);
        taxiListView.setPrefHeight(150);
        taxiListView.setPlaceholder(new Label("Click 'View All Taxis' to load."));

        HBox taxiButtons = new HBox(12);
        Button viewTaxisBtn = new Button("🚕  View All Taxis");
        viewTaxisBtn.setStyle(AppStyles.BTN_SECONDARY);
        AppStyles.addHover(viewTaxisBtn, AppStyles.BTN_SECONDARY, AppStyles.BTN_SECONDARY_HOVER);

        Button assignPassengerBtn = new Button("🔗  Auto-Assign Passengers");
        assignPassengerBtn.setStyle(AppStyles.BTN_SECONDARY);
        AppStyles.addHover(assignPassengerBtn, AppStyles.BTN_SECONDARY, AppStyles.BTN_SECONDARY_HOVER);

        taxiButtons.getChildren().addAll(viewTaxisBtn, assignPassengerBtn);

        // Departures section
        Separator sep1 = new Separator();
        Label depLabel = new Label("DEPARTURES");
        depLabel.setStyle(AppStyles.LABEL);

        ListView<String> departureListView = new ListView<>();
        departureListView.setStyle(AppStyles.LIST_VIEW);
        departureListView.setPrefHeight(150);
        departureListView.setPlaceholder(new Label("Click 'View Departures' to load."));

        HBox depButtons = new HBox(12);
        Button viewDepsBtn = new Button("✈  View Departures");
        viewDepsBtn.setStyle(AppStyles.BTN_SECONDARY);
        AppStyles.addHover(viewDepsBtn, AppStyles.BTN_SECONDARY, AppStyles.BTN_SECONDARY_HOVER);

        Button autoDepBtn = new Button("⚡  Check Auto Departure");
        autoDepBtn.setStyle(AppStyles.BTN_PRIMARY);
        AppStyles.addHover(autoDepBtn, AppStyles.BTN_PRIMARY, AppStyles.BTN_PRIMARY_HOVER);

        Button manualDepBtn = new Button("🔑  Force Manual Departure");
        manualDepBtn.setStyle(AppStyles.BTN_DANGER);
        AppStyles.addHover(manualDepBtn, AppStyles.BTN_DANGER, AppStyles.BTN_DANGER);

        depButtons.getChildren().addAll(viewDepsBtn, autoDepBtn, manualDepBtn);

        // Status label
        Label statusLabel = new Label("");
        statusLabel.setWrapText(true);
        statusLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-size: 12px;");

        card.getChildren().addAll(
                sectionTitle,
                taxisLabel, taxiListView, taxiButtons,
                sep1,
                depLabel, departureListView, depButtons,
                statusLabel
        );

        content.getChildren().add(card);

        // ── Actions ───────────────────────────────────────────────────────────
        viewTaxisBtn.setOnAction(e -> viewTaxis(taxiListView, statusLabel));
        assignPassengerBtn.setOnAction(e -> assignPassengerToTaxi(statusLabel));
        viewDepsBtn.setOnAction(e -> viewDepartures(departureListView, statusLabel));
        autoDepBtn.setOnAction(e -> autoDepartIfNeeded(statusLabel));
        manualDepBtn.setOnAction(e -> showManualDepartureDialog(statusLabel));

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: #1a1a2e; -fx-background-color: #1a1a2e;");
        return scroll;
    }

    // ── DB Logic ──────────────────────────────────────────────────────────────
    private void viewTaxis(ListView<String> listView, Label statusLabel) {
        listView.getItems().clear();
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT t.id, t.license_plate, t.destination, t.available, u.name AS driver_name " +
                     "FROM Taxis t LEFT JOIN Users u ON t.driver_id = u.id " +
                     "ORDER BY t.available DESC, t.destination");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String avail = rs.getBoolean("available") ? "🟢" : "🔴";
                String driver = rs.getString("driver_name") != null ? rs.getString("driver_name") : "Unassigned";
                listView.getItems().add(
                        avail + " #" + rs.getInt("id") +
                        " | " + rs.getString("license_plate") +
                        "  →  " + rs.getString("destination") +
                        "  | Driver: " + driver
                );
            }

            setStatus(statusLabel, listView.getItems().isEmpty()
                    ? "No taxis registered."
                    : listView.getItems().size() + " taxi(s) loaded.", !listView.getItems().isEmpty());

        } catch (SQLException e) {
            setStatus(statusLabel, "Error: " + e.getMessage(), false);
        }
    }

    private void viewDepartures(ListView<String> listView, Label statusLabel) {
        listView.getItems().clear();
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT d.id, t.license_plate, t.destination, p.name AS passenger, d.departure_time " +
                     "FROM Departures d " +
                     "JOIN Taxis t ON d.taxi_id = t.id " +
                     "JOIN Passengers p ON d.passenger_id = p.id " +
                     "ORDER BY d.departure_time DESC LIMIT 100");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                listView.getItems().add(
                        "✈ " + rs.getString("license_plate") +
                        "  →  " + rs.getString("destination") +
                        "  | 👤 " + rs.getString("passenger") +
                        "  | 🕐 " + rs.getTimestamp("departure_time")
                );
            }

            setStatus(statusLabel, listView.getItems().isEmpty()
                    ? "No departures recorded yet."
                    : listView.getItems().size() + " departure record(s) loaded.", !listView.getItems().isEmpty());

        } catch (SQLException e) {
            setStatus(statusLabel, "Error: " + e.getMessage(), false);
        }
    }

    private void assignPassengerToTaxi(Label statusLabel) {
        try (Connection conn = DBConfig.getConnection()) {
            String query =
                "INSERT INTO Departures (taxi_id, passenger_id, departure_time) " +
                "SELECT t.id, p.id, NOW() " +
                "FROM Taxis t JOIN Passengers p ON p.destination = t.destination " +
                "WHERE p.taxi_id IS NULL AND t.available = TRUE LIMIT 1";

            int rows = conn.prepareStatement(query).executeUpdate();
            setStatus(statusLabel,
                    rows > 0 ? "✅ Passenger assigned to a taxi." : "⚠ No matching taxi and passenger found.",
                    rows > 0);
        } catch (SQLException e) {
            setStatus(statusLabel, "Error: " + e.getMessage(), false);
        }
    }

    private void autoDepartIfNeeded(Label statusLabel) {
        try (Connection conn = DBConfig.getConnection()) {
            String countQuery =
                "SELECT destination, COUNT(*) AS total FROM Passengers " +
                "WHERE taxi_id IS NULL GROUP BY destination HAVING total >= 16";
            PreparedStatement countStmt = conn.prepareStatement(countQuery);
            ResultSet rs = countStmt.executeQuery();

            boolean anyDeparted = false;
            while (rs.next()) {
                String destination = rs.getString("destination");

                PreparedStatement taxiStmt = conn.prepareStatement(
                        "SELECT id FROM Taxis WHERE destination = ? AND available = TRUE LIMIT 1");
                taxiStmt.setString(1, destination);
                ResultSet taxiRs = taxiStmt.executeQuery();

                if (taxiRs.next()) {
                    int taxiId = taxiRs.getInt("id");

                    PreparedStatement assignStmt = conn.prepareStatement(
                            "INSERT INTO Departures (taxi_id, passenger_id, departure_time) " +
                            "SELECT ?, p.id, NOW() FROM Passengers p " +
                            "WHERE p.destination = ? AND p.taxi_id IS NULL LIMIT 16");
                    assignStmt.setInt(1, taxiId);
                    assignStmt.setString(2, destination);

                    if (assignStmt.executeUpdate() > 0) {
                        PreparedStatement updateStmt = conn.prepareStatement(
                                "UPDATE Taxis SET available = FALSE WHERE id = ?");
                        updateStmt.setInt(1, taxiId);
                        updateStmt.executeUpdate();
                        anyDeparted = true;
                    }
                }
            }
            setStatus(statusLabel,
                    anyDeparted
                            ? "✅ Auto-departure triggered for qualifying routes."
                            : "ℹ No routes have reached the 16-passenger threshold yet.",
                    anyDeparted);
        } catch (SQLException e) {
            setStatus(statusLabel, "Error: " + e.getMessage(), false);
        }
    }

    private void showManualDepartureDialog(Label statusLabel) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Force Manual Departure");
        dialog.setHeaderText("Enter the taxi details to force a departure.");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(16));
        vbox.setStyle(AppStyles.SCENE_BG);

        Label plateLabel = new Label("LICENSE PLATE");
        plateLabel.setStyle(AppStyles.LABEL);
        TextField licenseField = new TextField();
        licenseField.setPromptText("e.g. ET-3-12345");
        licenseField.setStyle(AppStyles.TEXT_FIELD);

        Label destLabel = new Label("DESTINATION");
        destLabel.setStyle(AppStyles.LABEL);
        TextField destField = new TextField();
        destField.setPromptText("e.g. Airport");
        destField.setStyle(AppStyles.TEXT_FIELD);

        vbox.getChildren().addAll(plateLabel, licenseField, destLabel, destField);
        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().setStyle(AppStyles.SCENE_BG);

        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                String license = licenseField.getText().trim();
                String dest    = destField.getText().trim();
                if (!license.isEmpty() && !dest.isEmpty()) {
                    manuallyDepartTaxi(license, dest, statusLabel);
                } else {
                    setStatus(statusLabel, "⚠ Both fields are required.", false);
                }
            }
        });
    }

    private void manuallyDepartTaxi(String license, String destination, Label statusLabel) {
        try (Connection conn = DBConfig.getConnection()) {
            String assignQuery =
                "INSERT INTO Departures (taxi_id, passenger_id, departure_time) " +
                "SELECT t.id, p.id, NOW() FROM Taxis t JOIN Passengers p ON p.destination = t.destination " +
                "WHERE t.license_plate = ? AND t.destination = ? AND p.taxi_id IS NULL";

            PreparedStatement stmt = conn.prepareStatement(assignQuery);
            stmt.setString(1, license);
            stmt.setString(2, destination);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                PreparedStatement updateStmt = conn.prepareStatement(
                        "UPDATE Taxis SET available = FALSE WHERE license_plate = ?");
                updateStmt.setString(1, license);
                updateStmt.executeUpdate();
                setStatus(statusLabel,
                        "✅ Taxi " + license + " forced departure to " + destination + " (" + rows + " passengers).",
                        true);
            } else {
                setStatus(statusLabel, "✗ No valid taxi or unassigned passengers found.", false);
            }
        } catch (SQLException e) {
            setStatus(statusLabel, "Error: " + e.getMessage(), false);
        }
    }

    // ── Utility ───────────────────────────────────────────────────────────────
    private void setStatus(Label label, String message, boolean success) {
        label.setStyle(success
                ? "-fx-text-fill: #2ecc71; -fx-font-size: 12px;"
                : "-fx-text-fill: #e94560; -fx-font-size: 12px;");
        label.setText(message);
    }
}

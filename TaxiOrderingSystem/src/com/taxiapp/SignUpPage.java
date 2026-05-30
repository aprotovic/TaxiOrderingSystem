package com.taxiapp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;

public class SignUpPage {

    public void showSignUpForm(Stage stage) {

        // ── Root ──────────────────────────────────────────────────────────────
        StackPane root = new StackPane();
        root.setStyle(AppStyles.SCENE_BG);

        // ── Card ──────────────────────────────────────────────────────────────
        VBox card = new VBox(12);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setMaxWidth(380);
        card.setStyle(AppStyles.CARD);

        Label title = new Label("🚕 TaxiGo");
        title.setStyle(AppStyles.TITLE);
        title.setAlignment(Pos.CENTER);

        Label subtitle = new Label("Create a new account");
        subtitle.setStyle(AppStyles.SUBTITLE);

        Region spacer1 = new Region(); spacer1.setMinHeight(6);

        // Full Name
        Label nameLabel = new Label("FULL NAME");
        nameLabel.setStyle(AppStyles.LABEL);
        TextField nameField = new TextField();
        nameField.setPromptText("e.g. Abebe Kebede");
        nameField.setStyle(AppStyles.TEXT_FIELD);
        nameField.setMaxWidth(Double.MAX_VALUE);

        // Username
        Label usernameLabel = new Label("USERNAME");
        usernameLabel.setStyle(AppStyles.LABEL);
        TextField usernameField = new TextField();
        usernameField.setPromptText("Choose a unique username");
        usernameField.setStyle(AppStyles.TEXT_FIELD);
        usernameField.setMaxWidth(Double.MAX_VALUE);

        // Password
        Label passwordLabel = new Label("PASSWORD");
        passwordLabel.setStyle(AppStyles.LABEL);
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("At least 6 characters");
        passwordField.setStyle(AppStyles.TEXT_FIELD);
        passwordField.setMaxWidth(Double.MAX_VALUE);

        // Confirm Password
        Label confirmLabel = new Label("CONFIRM PASSWORD");
        confirmLabel.setStyle(AppStyles.LABEL);
        PasswordField confirmField = new PasswordField();
        confirmField.setPromptText("Re-enter your password");
        confirmField.setStyle(AppStyles.TEXT_FIELD);
        confirmField.setMaxWidth(Double.MAX_VALUE);

        // Role — dropdown prevents free-text injection
        Label roleLabel = new Label("ROLE");
        roleLabel.setStyle(AppStyles.LABEL);
        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("passenger", "driver");
        roleCombo.setValue("passenger");
        roleCombo.setMaxWidth(Double.MAX_VALUE);
        roleCombo.setStyle(AppStyles.COMBO_BOX);

        // Error label
        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: #e94560; -fx-font-size: 12px;");
        errorLabel.setWrapText(true);
        errorLabel.setVisible(false);

        Region spacer2 = new Region(); spacer2.setMinHeight(4);

        // Buttons
        Button signUpButton = new Button("Create Account");
        signUpButton.setMaxWidth(Double.MAX_VALUE);
        AppStyles.addHover(signUpButton, AppStyles.BTN_PRIMARY, AppStyles.BTN_PRIMARY_HOVER);

        Button backButton = new Button("← Back to Sign In");
        backButton.setMaxWidth(Double.MAX_VALUE);
        AppStyles.addHover(backButton, AppStyles.BTN_SECONDARY, AppStyles.BTN_SECONDARY_HOVER);

        card.getChildren().addAll(
                title, subtitle, spacer1,
                nameLabel, nameField,
                usernameLabel, usernameField,
                passwordLabel, passwordField,
                confirmLabel, confirmField,
                roleLabel, roleCombo,
                errorLabel, spacer2,
                signUpButton, backButton
        );

        root.getChildren().add(card);
        StackPane.setMargin(card, new Insets(40));

        // ── Actions ───────────────────────────────────────────────────────────
        signUpButton.setOnAction(e -> {
            errorLabel.setVisible(false);
            String name     = nameField.getText().trim();
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            String confirm  = confirmField.getText();
            String role     = roleCombo.getValue();

            // Validation
            if (name.isEmpty() || username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                errorLabel.setText("⚠ All fields are required.");
                errorLabel.setVisible(true);
                return;
            }
            if (password.length() < 6) {
                errorLabel.setText("⚠ Password must be at least 6 characters.");
                errorLabel.setVisible(true);
                return;
            }
            if (!password.equals(confirm)) {
                errorLabel.setText("⚠ Passwords do not match.");
                errorLabel.setVisible(true);
                return;
            }

            handleSignUp(name, username, password, role, stage, errorLabel);
        });

        backButton.setOnAction(e -> new SignInPage().showSignInForm(stage));

        Scene scene = new Scene(root, 480, 640);
        stage.setTitle("TaxiGo — Create Account");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private void handleSignUp(String name, String username, String password,
                              String role, Stage stage, Label errorLabel) {
        try (Connection conn = DBConfig.getConnection()) {
            // Check for duplicate username
            PreparedStatement check = conn.prepareStatement(
                    "SELECT id FROM Users WHERE username = ?");
            check.setString(1, username);
            if (check.executeQuery().next()) {
                errorLabel.setText("✗ Username already taken. Choose another.");
                errorLabel.setVisible(true);
                return;
            }

            String query = "INSERT INTO Users (name, username, password, role) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, username);
            stmt.setString(3, PasswordUtil.hash(password));  // Store hashed password
            stmt.setString(4, role);

            if (stmt.executeUpdate() > 0) {
                showAlert("✅ Account Created",
                        "Welcome, " + name + "! Your account has been created. You can now sign in.",
                        Alert.AlertType.INFORMATION);
                new SignInPage().showSignInForm(stage);
            } else {
                errorLabel.setText("✗ Sign-up failed. Please try again.");
                errorLabel.setVisible(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            errorLabel.setText("✗ Database error: " + e.getMessage());
            errorLabel.setVisible(true);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

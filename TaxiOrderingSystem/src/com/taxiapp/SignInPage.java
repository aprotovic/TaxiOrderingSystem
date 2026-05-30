package com.taxiapp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.*;

public class SignInPage {

    public void showSignInForm(Stage stage) {

        // ── Root background ───────────────────────────────────────────────────
        StackPane root = new StackPane();
        root.setStyle(AppStyles.SCENE_BG);

        // ── Card ──────────────────────────────────────────────────────────────
        VBox card = new VBox(14);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(360);
        card.setStyle(AppStyles.CARD);

        // App title
        Label title = new Label("🚕 TaxiGo");
        title.setStyle(AppStyles.TITLE);

        Label subtitle = new Label("Sign in to continue");
        subtitle.setStyle(AppStyles.SUBTITLE);

        Region spacer1 = new Region();
        spacer1.setMinHeight(6);

        // Username
        Label usernameLabel = new Label("USERNAME");
        usernameLabel.setStyle(AppStyles.LABEL);
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        usernameField.setStyle(AppStyles.TEXT_FIELD);
        usernameField.setMaxWidth(Double.MAX_VALUE);

        // Password
        Label passwordLabel = new Label("PASSWORD");
        passwordLabel.setStyle(AppStyles.LABEL);
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setStyle(AppStyles.TEXT_FIELD);
        passwordField.setMaxWidth(Double.MAX_VALUE);

        // Error label (hidden until needed)
        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: #e94560; -fx-font-size: 12px;");
        errorLabel.setVisible(false);

        Region spacer2 = new Region();
        spacer2.setMinHeight(4);

        // Sign In button
        Button signInButton = new Button("Sign In");
        signInButton.setMaxWidth(Double.MAX_VALUE);
        AppStyles.addHover(signInButton, AppStyles.BTN_PRIMARY, AppStyles.BTN_PRIMARY_HOVER);

        // Divider
        HBox divider = new HBox(8);
        divider.setAlignment(Pos.CENTER);
        Separator sep1 = new Separator(); sep1.setPrefWidth(80);
        Separator sep2 = new Separator(); sep2.setPrefWidth(80);
        Label orLabel = new Label("OR");
        orLabel.setStyle(AppStyles.SUBTITLE);
        divider.getChildren().addAll(sep1, orLabel, sep2);

        // Sign Up link
        HBox signUpRow = new HBox(6);
        signUpRow.setAlignment(Pos.CENTER);
        Label noAccountLabel = new Label("Don't have an account?");
        noAccountLabel.setStyle(AppStyles.SUBTITLE);
        Button signUpButton = new Button("Create Account");
        signUpButton.setStyle(AppStyles.LINK_BUTTON);

        signUpRow.getChildren().addAll(noAccountLabel, signUpButton);

        card.getChildren().addAll(
                title, subtitle, spacer1,
                usernameLabel, usernameField,
                passwordLabel, passwordField,
                errorLabel, spacer2,
                signInButton, divider, signUpRow
        );

        root.getChildren().add(card);
        StackPane.setMargin(card, new Insets(40));

        // ── Actions ───────────────────────────────────────────────────────────
        signInButton.setOnAction(e -> {
            errorLabel.setVisible(false);
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            if (username.isEmpty() || password.isEmpty()) {
                errorLabel.setText("⚠ Please fill in all fields.");
                errorLabel.setVisible(true);
                return;
            }
            handleLogin(username, password, stage, errorLabel);
        });

        // Allow Enter key to trigger sign-in
        passwordField.setOnAction(e -> signInButton.fire());

        signUpButton.setOnAction(e -> new SignUpPage().showSignUpForm(stage));

        Scene scene = new Scene(root, 480, 540);
        stage.setTitle("TaxiGo — Sign In");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private void handleLogin(String username, String password, Stage stage, Label errorLabel) {
        try (Connection conn = DBConfig.getConnection()) {
            String query = "SELECT * FROM Users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            // NOTE: In production use BCrypt hashing. For now matching stored plain/hashed value.
            stmt.setString(2, PasswordUtil.hash(password));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String role = rs.getString("role");
                String name = rs.getString("name");
                int    userId = rs.getInt("id");

                switch (role.toLowerCase()) {
                    case "passenger":
                        new PassengerPage(username, userId).showPassengerPage(stage);
                        break;
                    case "driver":
                        new DriverPage(username, userId).showDriverPage(stage);
                        break;
                    case "admin":
                        new AdminPage().showAdminPage(stage);
                        break;
                    default:
                        errorLabel.setText("Unknown role: " + role);
                        errorLabel.setVisible(true);
                }
            } else {
                errorLabel.setText("✗ Incorrect username or password.");
                errorLabel.setVisible(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            errorLabel.setText("✗ Database error: " + e.getMessage());
            errorLabel.setVisible(true);
        }
    }
}

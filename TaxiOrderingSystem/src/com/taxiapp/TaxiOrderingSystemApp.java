package com.taxiapp;

import javafx.application.Application;
import javafx.stage.Stage;



public class TaxiOrderingSystemApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        SignInPage signInPage = new SignInPage();
        signInPage.showSignInForm(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

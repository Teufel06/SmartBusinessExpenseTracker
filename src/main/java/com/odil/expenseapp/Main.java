package com.odil.expenseapp;

import com.odil.expenseapp.db.Database;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Database.init(); // ensure DB & table
        FXMLLoader fxml = new FXMLLoader(getClass().getResource("/com/odil/expenseapp/dashboard.fxml"));
        Scene scene = new Scene(fxml.load(), 980, 620);
        scene.getStylesheets().add(getClass().getResource("/com/odil/expenseapp/styles.css").toExternalForm());
        stage.setTitle("Smart Business Expense Tracker");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) { launch(args); }
}

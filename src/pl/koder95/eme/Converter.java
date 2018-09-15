/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.koder95.eme;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * Klasa dziedziczy po {@link Application}.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 1.0.0, 2018-09-15
 * @since 1.0.0
 * @see Application
 */
public class Converter extends Application {
    
    private Parent root;
    
    @Override
    public void init() throws Exception {
        root = FXMLLoader.load(Objects.requireNonNull(FXMLLoader.getDefaultClassLoader()
                .getResource("pl/koder95/eme/conv/fx/CSVSettingsView.fxml")));
    }
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("eMetrykant Converter");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}

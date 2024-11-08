package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/org/example/demo/Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setMaximized(true);
        scene.getStylesheets().add(String.valueOf(getClass().getResource("/org/example/demo/Style.css")));
        stage.getIcons().add(new Image(String.valueOf(getClass().getResource("/org/example/demo/academi.png"))));
        stage.setTitle("Service");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
package com.pixelproteam.pixelpro;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        File logopath = new File("src/main/java/com/pixelproteam/pixelpro/logo.png");
        Image logo = new Image(logopath.toURI().toString());
        stage.getIcons().add(logo);
        //ReadOnlyDoubleProperty heit= stage.widthProperty();
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }


}
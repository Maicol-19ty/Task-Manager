package com.infinitehorizons.taskmanager;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {

    protected ObservableList<Image> icons = FXCollections.observableArrayList();

    @Override
    public void start(Stage stage) throws IOException {


        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.getIcons().add(new Image("file:src/main/resources/com/infinitehorizons/taskmanager/images/task-logo-2.png"));
        stage.getIcons().addAll(icons);
        stage.setTitle("Task Manager - Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
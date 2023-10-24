package com.infinitehorizons.taskmanager;

import com.infinitehorizons.taskmanager.DataBase.Connect;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.getIcons().add(new Image("file:src/main/java/com/infinitehorizons/taskmanager/Images/task-logo.jpeg"));
        stage.setTitle("Task Manager - Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
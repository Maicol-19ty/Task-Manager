package com.infinitehorizons.taskmanager.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class ForgotController implements Initializable {

    @FXML
    private ImageView forbrandingImageView;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Pestaña de recuerdo inicializada.");
        File forbrandingFile = new File("src/main/resources/com/infinitehorizons/taskmanager/images/task-logo-2.png");
        if (forbrandingFile.exists()) {
            Image forbrandingImage = new Image(forbrandingFile.toURI().toString());
            forbrandingImageView.setImage(forbrandingImage);
        } else {
            System.out.println("No se pudo encontrar la imagen en la ubicación especificada.");
        }

    }

}

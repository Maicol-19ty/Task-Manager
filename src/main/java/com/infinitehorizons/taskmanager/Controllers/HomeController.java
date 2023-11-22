package com.infinitehorizons.taskmanager.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    private String userName;
    @FXML
    private ImageView homeImage;
    @FXML
    private Label usernameHome;

    public HomeController(String userName) {
        this.userName = userName;
    }

    public void initialize (URL url, ResourceBundle resourceBundle) {
        File brandingFile = new File("src//main//resources//com//infinitehorizons//taskmanager//images//task-logo-2.png");
        Image brandingImage = new Image(brandingFile.toURI().toString());
        homeImage.setImage(brandingImage);

        usernameHome.setText(userName);
    }

}
